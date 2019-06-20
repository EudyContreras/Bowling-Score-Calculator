package com.eudycontreras.bowlingcalculator.adapters

import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameNormal
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll
import com.eudycontreras.bowlingcalculator.components.views.FramesViewComponent
import com.eudycontreras.bowlingcalculator.extensions.animateColor
import com.eudycontreras.bowlingcalculator.extensions.dp
import com.eudycontreras.bowlingcalculator.extensions.hide
import com.eudycontreras.bowlingcalculator.extensions.show
import com.eudycontreras.bowlingcalculator.runSequential
import kotlinx.android.synthetic.main.item_frame_view.view.*
import kotlinx.android.synthetic.main.item_frame_view_mark.view.*
import java.lang.ref.WeakReference

/**
 * Created by eudycontreras.
 */

class FrameViewAdapter(
    private val context: Activity,
    private val viewComponent: FramesViewComponent,
    private val items: ArrayList<Frame>
) : RecyclerView.Adapter<FrameViewAdapter.FrameViewHolder>() {

    internal val viewHolders: Array<FrameViewHolder?> = arrayOfNulls(10)

    internal val missIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_result_miss)
    internal val spareIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_result_spare)
    internal val strikeIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_result_strike)

    internal val markerNormal: Int = ContextCompat.getColor(context, R.color.frameMark)
    internal val markerSelected: Int = ContextCompat.getColor(context, R.color.frameMarkSelected)

    internal val white: Int = ContextCompat.getColor(context, R.color.white)
    internal val semiWhite: Int = ContextCompat.getColor(context, R.color.background)

    internal var lastReference: WeakReference<FrameViewHolder>? = null

    internal var currentIndex: Int = DEFAULT_START_INDEX
    internal var lastSelected: Int? = null

    private var resetFlipSpeed: Long = 500
    private var revealSpeed: Long = 200

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
        return FrameViewHolder(LayoutInflater.from(context).inflate(R.layout.item_frame_view, parent, false))
    }

    override fun onViewRecycled(holder: FrameViewHolder) {
        super.onViewRecycled(holder)
        holder.resetValues()
    }

    override fun onBindViewHolder(holder: FrameViewHolder, position: Int) {
        holder.resetValues()
        holder.performBinding(this, items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal fun adjustViewPort(index: Int) {
        if (index != currentIndex) {
            return
        }

        if ((index > (itemCount / 2)) || index == DEFAULT_START_INDEX) {
            this.viewComponent.scrollToIndex(index)
        }
    }

    internal fun resetAllFrames() {
        lastSelected = null
        lastReference = null
        currentIndex = DEFAULT_START_INDEX
        adjustViewPort(0)
        runSequential(80, viewHolders.size) {
            context.runOnUiThread { viewHolders[it]?.resetCell(resetFlipSpeed) }
        }
    }

    internal fun revealAllFrames() {
        adjustViewPort(0)
        runSequential(120, viewHolders.size) {
            context.runOnUiThread { viewHolders[it]?.revealCell(revealSpeed, this) }
        }
    }

    fun changeSource(items: List<Frame>) {
        this.lastReference = null
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    class FrameViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val frameIndex: TextView = view.frameIndex
        private val frameScore: TextView = view.frameScore

        private val roundOneMarkIcon: View = view.firstRoundMark.roundMarkIcon
        private val roundTwoMarkIcon: View = view.secondRoundMark.roundMarkIcon
        private val roundExtraMarkIcon: View = view.extraRoundMark.roundMarkIcon

        private val roundOneMarkText: TextView = view.firstRoundMark.roundMarkText
        private val roundTwoMarkText: TextView = view.secondRoundMark.roundMarkText
        private val roundExtraMarkText: TextView = view.extraRoundMark.roundMarkText

        private val roundOneMark: FrameLayout = view.firstRoundMark as FrameLayout
        private val roundTwoMark: FrameLayout = view.secondRoundMark as FrameLayout
        private val roundExtraMark: FrameLayout = view.extraRoundMark as FrameLayout

        private var adapter: FrameViewAdapter? = null

        private var frame: Frame? = null

        private val markDefaultScale: Float = 0.9f

        init {
            view.setOnClickListener(this)

            roundOneMark.isClickable = false
            roundTwoMark.isClickable = false
            roundExtraMark.isClickable = false
        }

        override fun onClick(view: View?) {
            frame?.let {frame ->
                adapter?.let {
                    if (it.currentIndex != frame.index) {
                        it.currentIndex = frame.index
                        it.lastSelected?.let { last ->
                            if (it.viewComponent.controller.canSelect(frame.index, last)) {
                                it.viewComponent.controller.performFrameSelection(frame.index)
                                it.lastReference?.let { reference ->
                                    if (!reference.isEnqueued) {
                                        sendLastToBack(reference.get(), it)
                                    }
                                }
                                it.lastReference = WeakReference(this)
                                bringCurrentToFront(frame, it)
                            }
                        }
                        return
                    }
                    if(frame is FrameLast && frame.isCompleted) {
                        it.viewComponent.controller.performFrameSelection(frame.index)
                        bringCurrentToFront(frame, it)
                    }
                }
            }
        }

        fun resetCell(flipSpeed: Long, onEnd: (() -> Unit)? = null) {
            frame?.let {
                animateResetFrameCell(flipSpeed) {
                    onEnd?.invoke()
                    resetValues()
                }
            }
        }

        fun revealCell(
            flipSpeed: Long,
            adapter: FrameViewAdapter
        ) {
            frame?.let {
                animateFrameReveal(flipSpeed, adapter)
            }
        }

        internal fun resetValues() {

            adapter?.let {
                if (it.items.isEmpty()){
                    frame = null
                }
                view.animateColor(it.white, it.semiWhite, 0L)
                disableMarkSelection(this, it)
                frame?.let {frame ->
                    performBinding(it, frame)
                }
            }

            frame?.let {
                if (it is FrameNormal) {
                    roundExtraMark.hide()
                }
            }

            view.translationZ = 0f
            view.translationY = 0f

            roundOneMark.scaleX = markDefaultScale
            roundOneMark.scaleY = markDefaultScale

            roundTwoMark.scaleX = markDefaultScale
            roundTwoMark.scaleY = markDefaultScale

            roundExtraMark.scaleX = markDefaultScale
            roundExtraMark.scaleY = markDefaultScale

            roundOneMarkIcon.background = null
            roundTwoMarkIcon.background = null
            roundExtraMarkIcon.background = null

            roundOneMarkText.text = ""
            roundTwoMarkText.text = ""
            roundExtraMarkText.text = ""

            frameScore.text = "0"
        }

        internal fun performBinding(adapter: FrameViewAdapter, frame: Frame) {
            this.frame = frame
            this.adapter = adapter

            if (!adapter.viewHolders.any { holder -> holder?.layoutPosition == frame.index }) {
                view.translationZ = 30.dp
                view.translationY = (-20).dp
                view.scaleX = 1.3f
                view.scaleY = 1.3f
                view.alpha = 0f
            }

            adapter.viewHolders[frame.index] = this

            frameIndex.text = (frame.index + 1).toString()
            frameScore.text = frame.getTotal(true).toString()

            if (frame.index == adapter.currentIndex) {
                adapter.lastReference = WeakReference(this)
                bringCurrentToFront(frame, adapter)
            } else if (frame.index == adapter.currentIndex - 1) {
                adapter.lastSelected?.let {
                    if (frame.index == it) {
                        sendLastToBack(this, adapter)
                    }
                }
            }

            if (frame is FrameLast) {
                if (!frame.isCompleted) {
                    roundExtraMark.show()
                } else {
                    if (frame.getRollBy(Frame.State.EXTRA_CHANCE) == null){
                        roundExtraMark.hide()
                    }
                    sendLastToBack(this, adapter)
                }
            } else {
                roundExtraMark.hide()
            }

            if (frame.rolls.isEmpty()) {
                return
            }

            determineValues(adapter, frame, frame.getRollBy(Frame.State.FIRST_CHANCE), roundOneMarkIcon, roundOneMarkText)
            determineValues(adapter, frame, frame.getRollBy(Frame.State.SECOND_CHANCE), roundTwoMarkIcon, roundTwoMarkText)
            determineValues(adapter, frame, frame.getRollBy(Frame.State.EXTRA_CHANCE), roundExtraMarkIcon, roundExtraMarkText)
        }

        private fun processMarkerSelection(frame: Frame, adapter: FrameViewAdapter) {
            when (frame.state) {
                Frame.State.FIRST_CHANCE -> {
                    animateMarkSelected(roundOneMark, adapter)
                }
                Frame.State.SECOND_CHANCE -> {
                    animateMarkUnselected(roundOneMark, adapter)
                    animateMarkSelected(roundTwoMark, adapter)
                }
                Frame.State.EXTRA_CHANCE -> {
                    animateMarkUnselected(roundOneMark, adapter)
                    animateMarkUnselected(roundTwoMark, adapter)
                    animateMarkSelected(roundExtraMark, adapter)
                }
                else -> {
                    animateMarkUnselected(roundOneMark, adapter)
                    animateMarkUnselected(roundTwoMark, adapter)
                    animateMarkUnselected(roundExtraMark, adapter)
                }
            }
        }

        private fun determineValues(adapter: FrameViewAdapter, frame: Frame, roll: Roll?, icon: View, text: TextView) {
            if (roll == null)
                return

            when (roll.result) {
                Roll.Result.STRIKE -> {
                    icon.show()
                    icon.background = adapter.strikeIcon
                    text.hide()
                    if (frame is FrameNormal) {
                        roundTwoMark.hide()
                    }
                }
                Roll.Result.SPARE -> {
                    icon.show()
                    icon.background = adapter.spareIcon
                    text.hide()
                }
                Roll.Result.MISS -> {
                    icon.show()
                    icon.background = adapter.missIcon
                    text.hide()
                }
                Roll.Result.NORMAL -> {
                    icon.hide()
                    text.show()
                    text.text = roll.totalKnockdown.toString()
                }
                else -> { }
            }
        }

        private fun bringCurrentToFront(frame: Frame, adapter: FrameViewAdapter) {
            if (adapter.viewComponent.controller.canSelect(frame.index)) {
                if (adapter.currentIndex == frame.index) {
                    if (adapter.lastSelected == null) {
                        bringCurrentToFront(view) { processMarkerSelection(frame, adapter ) }
                    } else {
                        adapter.lastSelected?.let {
                            if (it == frame.index) {
                                view.translationZ = 4.dp
                                view.translationY = (-1).dp
                                view.backgroundTintList = ColorStateList.valueOf(adapter.white)
                                adapter.lastSelected = frame.index
                                processMarkerSelection(frame, adapter )
                            } else {
                                bringCurrentToFront(view) { processMarkerSelection(frame, adapter ) }
                            }
                        }
                    }
                }
            }
        }

        private fun bringCurrentToFront(view: View?, onEnd: (() -> Unit)?) {
            adapter?.let {
                frame?.let { frame ->
                    it.lastSelected = frame.index
                }

                val duration: Long = 200
                view?.run {
                    animateColor(it.semiWhite, it.white, duration)
                    animate()
                        .setListener(null)
                        .translationZ(4.dp)
                        .translationY((-1).dp)
                        .setDuration(duration)
                        .start()
                }

                onEnd?.invoke()
            }

            roundOneMark.isClickable = true
            roundTwoMark.isClickable = true
            roundExtraMark.isClickable = true
        }

        private fun sendLastToBack(viewHolder: FrameViewHolder?, adapter: FrameViewAdapter) {
            viewHolder?.let {
                val view: View = it.itemView
                view.translationY = (-1).dp
                view.translationZ = 4.dp
                view.backgroundTintList = ColorStateList.valueOf(adapter.white)

                val duration: Long = 200
                view.animateColor(adapter.white, adapter.semiWhite, duration)
                disableMarkSelection(viewHolder, adapter)
                view.animate()
                    .setListener(null)
                    .translationZ(0.dp)
                    .translationY(0f)
                    .setDuration(duration)
                    .start()
            }
        }

        private fun disableMarkSelection(viewHolder: FrameViewHolder, adapter: FrameViewAdapter) {
            viewHolder.roundOneMark.isClickable = false
            viewHolder.roundTwoMark.isClickable = false
            viewHolder.roundExtraMark.isClickable = false

            animateMarkUnselected(viewHolder.roundOneMark, adapter)
            viewHolder.frame?.let {
                if (!it.rolls.values.any { roll -> roll.result == Roll.Result.STRIKE }) {
                    animateMarkUnselected(viewHolder.roundTwoMark, adapter)
                } else {
                    if (it is FrameLast){
                        animateMarkUnselected(viewHolder.roundTwoMark, adapter)
                    }
                }
            }
            animateMarkUnselected(viewHolder.roundExtraMark, adapter)
        }

        private fun animateFrameReveal(
            duration: Long,
            adapter: FrameViewAdapter
        ) {
            var translateY = 0f
            var translateZ = 0f

            frame?.let {
                if (adapter.currentIndex == it.index) {
                    translateY = (-1).dp
                    translateZ = 4.dp
                }
            }

            view.animate()
                .setInterpolator(DecelerateInterpolator())
                .setDuration(duration)
                .translationZ(translateZ)
                .translationY(translateY)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .start()
        }

        private fun animateResetFrameCell(flipSpeed: Long, onEnd: (() -> Unit)? = null) {
            val animator = ValueAnimator.ofFloat(0f, 360f)

            animator.duration = flipSpeed
            animator.interpolator = AccelerateDecelerateInterpolator()

            var showBack = false

            view.rotationX = 0f
            animator.addUpdateListener { anim ->
                val value: Float = anim.animatedValue as Float
                if (value >= 90 && value < 270) {
                    if (!showBack) {
                        showBack = true
                        roundOneMark.alpha = 0f
                        roundTwoMark.alpha = 0f
                        roundExtraMark.alpha = 0f
                        frameScore.alpha = 0f
                        frameIndex.alpha = 0f
                        onEnd?.invoke()
                    }
                } else {
                    if (showBack) {
                        showBack = false
                        roundOneMark.alpha = 1f
                        roundTwoMark.alpha = 1f
                        frame?.let {
                            if (it is FrameLast) {
                                roundExtraMark.alpha = 1f
                            }
                        }
                        frameScore.alpha = 1f
                        frameIndex.alpha = 1f
                    }
                }
                view.rotationX = value
            }
            animator.start()
        }

        private fun animateMarkSelected(mark: View, adapter: FrameViewAdapter) {
            val duration: Long = 200
            mark.animateColor(adapter.markerNormal, adapter.markerSelected, duration)
            mark.animate()
                .setListener(null)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(duration)
                .start()
        }

        private fun animateMarkUnselected(mark: View, adapter: FrameViewAdapter) {
            val duration: Long = 100
            mark.animateColor(adapter.markerSelected, adapter.markerNormal, 0)
            mark.animate()
                .setListener(null)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(duration)
                .start()
        }
    }
}
