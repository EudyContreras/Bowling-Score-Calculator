package com.eudycontreras.bowlingcalculator.components

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.adapters.FrameViewAdapter
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.components.controllers.FramesViewController
import com.eudycontreras.bowlingcalculatorchallenge.view_components.ViewComponent
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by eudycontreras.
 */

class FramesViewComponent(
    private val context: MainActivity,
    val controller: FramesViewController
) : ViewComponent {

    private lateinit var frameAdapter: FrameViewAdapter

    private val frameRecycler: RecyclerView? = context.framesArea as RecyclerView

    private var smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return LinearSmoothScroller.SNAP_TO_START
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
            frameAdapter = FrameViewAdapter(context, this, frames as ArrayList<Frame>)
            it.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            it.recycledViewPool.setMaxRecycledViews(0, 0)
            it.setItemViewCacheSize(10)
            it.adapter = frameAdapter
        }
    }

    fun resetFrames() {
        frameAdapter.resetAllFrames()
    }

    fun revealFrames() {
        frameAdapter.revealAllFrames()
    }

    fun updateFrames(bowler: Bowler, current: Frame) {
        frameAdapter.currentIndex = bowler.currentFrameIndex
        frameAdapter.adjustViewPort(current.index)
        frameAdapter.notifyDataSetChanged()
    }

    override fun registerListeners() {}

    override fun assignInteraction(view: View?) { }

}