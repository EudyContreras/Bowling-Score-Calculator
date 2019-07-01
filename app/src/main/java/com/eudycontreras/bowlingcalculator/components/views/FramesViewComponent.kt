package com.eudycontreras.bowlingcalculator.components.views

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.components.controllers.FramesViewController
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_FRAME_COUNT
import com.eudycontreras.bowlingcalculator.utilities.SCROLLER_MILLI_PER_INCH
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class FramesViewComponent(
    private val context: MainActivity,
    val controller: FramesViewController
) : ViewComponent {

    private lateinit var frameAdapter: FrameViewAdapter

    private val frameRecycler: RecyclerView? = context.framesArea as RecyclerView

    private var smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return  SNAP_TO_START
        }
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return SCROLLER_MILLI_PER_INCH / displayMetrics.densityDpi
        }
    }

    init {
        setDefaultValues()
    }

    override fun setDefaultValues() {}

    fun scrollToIndex(index: Int) {
        val layoutManager = frameRecycler?.layoutManager
        layoutManager?.let {
            smoothScroller.targetPosition = index
            layoutManager.startSmoothScroll(smoothScroller)
        }
    }

    fun createFrames(frames: List<Frame>) {
        frameRecycler?.let {
            val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            frameAdapter = FrameViewAdapter(context, layoutManager, this, frames as ArrayList<Frame>)
            it.layoutManager = layoutManager
            it.recycledViewPool.setMaxRecycledViews(0, 0)
            it.setItemViewCacheSize(DEFAULT_FRAME_COUNT)
            it.adapter = frameAdapter
        }
    }

    fun setSourceFrames(frames: List<Frame>) {
        if (this::frameAdapter.isInitialized) {
            frameAdapter.changeSource(frames)
        }
    }

    fun framesCreated(): Boolean {
        return this::frameAdapter.isInitialized
    }

    fun resetFrames() {
        frameAdapter.resetAllFrames()
    }

    fun revealFrames(bowler: Bowler) {
        frameAdapter.revealAllFrames(bowler)
    }

    fun concealFrames(onEnd: () -> Unit) {
        frameAdapter.concealFrames(onEnd)
    }

    fun updateFrames(bowler: Bowler, current: Frame) {
        frameAdapter.currentIndex = bowler.currentFrameIndex
        frameAdapter.adjustViewPort(current.index)
        frameAdapter.notifyDataSetChanged()
    }

    override fun registerListeners() {}

    override fun assignInteraction(view: View?) { }

}