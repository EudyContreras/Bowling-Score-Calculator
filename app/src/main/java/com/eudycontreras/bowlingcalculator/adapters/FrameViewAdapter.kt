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
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter.Values.Companion.concealSpeed
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter.Values.Companion.markDefaultScale
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter.Values.Companion.resetFlipSpeed
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter.Values.Companion.revealSpeed
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter.Values.Companion.sequenceStagger
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter.Values.Companion.toBackDuration
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter.Values.Companion.toFrontDuration
import com.eudycontreras.bowlingcalculator.calculator.elements.*
import com.eudycontreras.bowlingcalculator.components.views.FramesViewComponent
import com.eudycontreras.bowlingcalculator.listeners.AnimationListener
import com.eudycontreras.bowlingcalculator.utilities.*
import com.eudycontreras.bowlingcalculator.utilities.extensions.*
import kotlinx.android.synthetic.main.item_frame_view.view.*
import kotlinx.android.synthetic.main.item_frame_view_mark.view.*


/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class FrameViewAdapter(
    private val context: Activity,
    private val layoutManager: LinearLayoutManager,
    private val viewComponent: FramesViewComponent,
    private val items: ArrayList<Frame>
) : RecyclerView.Adapter<FrameViewAdapter.FrameViewHolder>() {

    internal val missIcon: Drawable = context.drawable(R.drawable.ic_result_miss)
    internal val spareIcon: Drawable = context.drawable(R.drawable.ic_result_spare)
    internal val strikeIcon: Drawable = context.drawable(R.drawable.ic_result_strike)

    internal val markerNormal: ColorStateList = ColorStateList.valueOf(context.color(R.color.frameMark))
    internal val markerSelected: ColorStateList = ColorStateList.valueOf(context.color(R.color.frameMarkSelected))

    internal val white: ColorStateList = ColorStateList.valueOf(context.color(R.color.white))
    internal val semiWhite: ColorStateList = ColorStateList.valueOf(context.color(R.color.background))

    internal var revealingBowlerId: Long = -1
    internal var currentIndex: Int = DEFAULT_START_INDEX
    internal var lastSelected: Int? = null

    private var revealed: HashMap<Int, Boolean> = HashMap(DEFAULT_FRAME_COUNT)

    private val viewHolders: Array<FrameViewHolder?> = arrayOfNulls(DEFAULT_FRAME_COUNT)

    private var lastReference: FrameViewHolder? = null

    internal sealed class Values {
        companion object {
            const val toFrontDuration = 250L
            const val toBackDuration = 250L
            const val sequenceStagger: Long = 50
            const val resetFlipSpeed: Long = 800
            const val revealSpeed: Long = 300
            const val concealSpeed: Long = 300
            const val markDefaultScale: Float = 0.9f
        }
    }

    init {
        (0..DEFAULT_FRAME_COUNT).forEach { revealed[it] = false }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
        return FrameViewHolder(LayoutInflater.from(context).inflate(R.layout.item_frame_view, parent, false))
    }

    override fun onViewRecycled(holder: FrameViewHolder) {
        super.onViewRecycled(holder)
        holder.resetValues(this)
    }

    override fun onBindViewHolder(holder: FrameViewHolder, position: Int) {
        holder.resetValues(this)
        holder.performBinding(this, items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal fun adjustViewPort(index: Int) {
        val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()
        val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()

        if (index in firstVisible..lastVisible) {
            return
        }

        if (index != currentIndex) {
            return
        }

        if (index > (itemCount / 2)) {
            this.viewComponent.scrollToIndex(itemCount - 1)
            return
        }

        if (index < (itemCount / 2)) {
            this.viewComponent.scrollToIndex(DEFAULT_START_INDEX)
        }
    }

    internal fun resetAllFrames() {
        lastSelected = null
        currentIndex = DEFAULT_START_INDEX
        adjustViewPort(0)
        runSequential(sequenceStagger, items.size) {
            viewHolders[it]?.resetCell(this, resetFlipSpeed, items[it])
        }
    }

    internal fun revealAllFrames(bowler: Bowler) {
        revealingBowlerId = bowler.id
        adjustViewPort(0)
        runSequential(sequenceStagger, items.size) {
            revealed[it] = true
            viewHolders[it]?.revealCell(this, revealSpeed, items[it])
        }
    }

    fun concealFrames(onEnd: () -> Unit) {
        for (it in 0 until items.size) {
            revealed[it] = false
            viewHolders[it]?.concealCell(revealSpeed)
        }
        runAfterMain(concealSpeed, onEnd)
    }

    fun changeSource(items: List<Frame>) {
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

        private var selected: Boolean = false

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            if (frame == null || adapter == null)
                return

            doWith(frame, adapter){ frame, adapter->
                if (adapter.currentIndex != frame.index) {
                    adapter.currentIndex = frame.index
                    if (adapter.viewComponent.controller.checkCanSelectFrame(frame.index, adapter.lastSelected)) {
                        adapter.viewComponent.controller.selectFrame(frame.index)
                        adapter.lastReference?.let { reference ->
                            sendLastToBack(reference, adapter)
                        }
                        bringCurrentToFront(frame, adapter)
                    }
                    return
                }
                if(frame is FrameLast && frame.isCompleted) {
                    adapter.viewComponent.controller.selectFrame(frame.index)
                    bringCurrentToFront(frame, adapter)
                }
            }
        }

        internal fun resetValues(adapter: FrameViewAdapter) {
            view.scaleX = 1f
            view.scaleY = 1f
            view.translationZ = 0f
            view.translationY = 0f
            view.alpha = 1f

            roundOneMark.scaleX = markDefaultScale
            roundOneMark.scaleY = markDefaultScale

            roundTwoMark.scaleX = markDefaultScale
            roundTwoMark.scaleY = markDefaultScale

            roundExtraMark.scaleX = markDefaultScale
            roundExtraMark.scaleY = markDefaultScale

            roundOneMark.backgroundTintList = adapter.markerNormal
            roundTwoMark.backgroundTintList = adapter.markerNormal
            roundExtraMark.backgroundTintList = adapter.markerNormal

            view.backgroundTintList = adapter.semiWhite

            roundOneMarkIcon.background = null
            roundTwoMarkIcon.background = null
            roundExtraMarkIcon.background = null

            roundOneMarkText.text = ""
            roundTwoMarkText.text = ""
            roundExtraMarkText.text = ""

            frameScore.text = "0"

            frame?.let {
                if (it is FrameNormal) {
                    roundExtraMark.hide()
                }
            }
        }

        internal fun performBinding(adapter: FrameViewAdapter, frame: Frame) {
            this.frame = frame
            this.adapter = adapter

            adapter.viewHolders[frame.index] = this

            frameIndex.text = toString(frame.index + 1)
            frameScore.text = toString(frame.getTotal(true))

            if (!adapter.revealed.getValue(frame.index)) {
                itemView.translationZ = 30.dp
                itemView.translationY = (-60).dp
                itemView.scaleX = 1.2f
                itemView.scaleY = 1.2f
                itemView.alpha = 0f
            }

            if (frame.index == adapter.currentIndex) {
                if (adapter.revealingBowlerId != frame.bowlerId) {
                    bringCurrentToFront(frame, adapter)
                } else {
                    if (adapter.revealed.getValue(frame.index)) {
                        bringCurrentToFront(frame, adapter)
                    }
                }
            } else {
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

        private fun setMarkerSelection(frame: Frame, adapter: FrameViewAdapter) {
            when (frame.state) {
                Frame.State.FIRST_CHANCE -> {
                    setMarkSelected(roundOneMark, adapter)
                }
                Frame.State.SECOND_CHANCE -> {
                    setMarkUnselected(roundOneMark, adapter)
                    setMarkSelected(roundTwoMark, adapter)
                }
                Frame.State.EXTRA_CHANCE -> {
                    setMarkUnselected(roundOneMark, adapter)
                    setMarkUnselected(roundTwoMark, adapter)
                    setMarkSelected(roundExtraMark, adapter)
                }
                else -> {
                    setMarkUnselected(roundOneMark, adapter)
                    setMarkUnselected(roundTwoMark, adapter)
                    setMarkUnselected(roundExtraMark, adapter)
                }
            }
        }

        private fun animateMarkerSelection(frame: Frame, adapter: FrameViewAdapter) {
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
                    text.hide()
                    icon.show()
                    icon.background = adapter.strikeIcon
                    if (frame is FrameNormal) {
                        roundTwoMark.hide()
                    }
                }
                Roll.Result.SPARE -> {
                    text.hide()
                    icon.show()
                    icon.background = adapter.spareIcon
                }
                Roll.Result.MISS -> {
                    text.hide()
                    icon.show()
                    icon.background = adapter.missIcon
                }
                Roll.Result.NORMAL -> {
                    icon.hide()
                    text.show()
                    text.text = roll.totalKnockdown.toString()
                }
                else -> { }
            }
        }

        internal fun resetCell(adapter:FrameViewAdapter, flipSpeed: Long, frame: Frame, onEnd: (() -> Unit)? = null) {
            animateResetFrameCell(flipSpeed) {
                onEnd?.invoke()
                resetValues(adapter)
                performBinding(adapter, frame)
            }
        }

        internal fun revealCell(
            adapter: FrameViewAdapter,
            flipSpeed: Long,
            frame: Frame
        ) {
            animateFrameReveal(frame, flipSpeed, adapter)
        }

        internal fun concealCell(concealSpeed: Long) {
            animateFrameConceal(concealSpeed)
        }

        private fun bringCurrentToFront(frame: Frame, adapter: FrameViewAdapter) {

            if (!adapter.viewComponent.controller.checkCanSelectFrame(frame.index))
                return

            if (frame.isCompleted)
                return

            if (adapter.lastSelected == frame.index) {
                view.translationZ = 4.dp
                view.translationY = (-1).dp
                view.backgroundTintList = adapter.white

                adapter.lastReference = this
                adapter.lastSelected = frame.index
                setMarkerSelection(frame, adapter)
            } else {
                adapter.lastReference = this
                adapter.lastSelected = frame.index
                animateMarkerSelection(frame, adapter )
                bringCurrentToFront(view, adapter)
            }

            selected = true
        }

        private fun bringCurrentToFront(view: View, adapter: FrameViewAdapter, onEnd: (() -> Unit)? = null) {
            val duration: Long = toFrontDuration

            view.animateColor(adapter.semiWhite, adapter.white, duration)

            val listener: AnimationListener? = if (onEnd != null) {
                AnimationListener(onEnd = onEnd)
            } else
                null

            view.animate()
                .setListener(listener)
                .scaleX(1f)
                .scaleY(1f)
                .translationZ(4.dp)
                .translationY((-1).dp)
                .alpha(1f)
                .setDuration(duration)
                .start()
        }

        private fun sendLastToBack(viewHolder: FrameViewHolder?, adapter: FrameViewAdapter) {
            viewHolder?.let {
                view.translationY = (-1).dp
                view.translationZ = 4.dp
                view.backgroundTintList = adapter.white

                val duration: Long = toBackDuration
                val view: View = it.itemView

                view.animateColor(adapter.white, adapter.semiWhite, duration)

                view.animate()
                    .setListener(null)
                    .translationZ(0.dp)
                    .translationY(0f)
                    .setDuration(duration)
                    .start()

                disableMarkSelection(viewHolder, adapter)

                selected = false
            }
        }

        private fun disableMarkSelection(viewHolder: FrameViewHolder, adapter: FrameViewAdapter) {
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

        private fun animateFrameConceal(duration: Long) {
            view.animate()
                .setListener(null)
                .setInterpolator(LinearInterpolator())
                .setDuration(duration)
                .alpha(0f)
                .start()
        }

        private fun animateFrameReveal(
            frame: Frame,
            duration: Long,
            adapter: FrameViewAdapter
        ) {
            var translateY = 0f
            var translateZ = 0f

            if (adapter.currentIndex == frame.index) {
                translateY = (-1).dp
                translateZ = 4.dp
            }

            val onEnd: (() -> Unit)? = {
                if (frame.index == adapter.currentIndex && !frame.isCompleted) {
                    bringCurrentToFront(frame, adapter)
                }
            }

            itemView.animate()
                .setListener(AnimationListener(onEnd = onEnd))
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

        private fun setMarkSelected(mark: View, adapter: FrameViewAdapter) {
            mark.backgroundTintList = adapter.markerSelected
            mark.scaleX = 1f
            mark.scaleY = 1f
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

        private fun setMarkUnselected(mark: View, adapter: FrameViewAdapter) {
            mark.backgroundTintList = adapter.markerNormal
            mark.scaleX = 0.9f
            mark.scaleY = 0.9f
        }

        private fun animateMarkUnselected(mark: View, adapter: FrameViewAdapter) {
            val duration: Long = 150

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
