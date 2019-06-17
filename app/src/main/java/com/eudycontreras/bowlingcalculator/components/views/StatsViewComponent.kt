package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import android.widget.TextView
import com.eudycontreras.bowlingcalculator.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.StatsViewController
import com.eudycontreras.bowlingcalculator.toString
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by eudycontreras.
 */

class StatsViewComponent(
    context: MainActivity,
    private val controller: StatsViewController
) : ViewComponent {

    private val parentView: View? = context.statsArea

    private val currentFrameStat: TextView? = parentView?.findViewById(R.id.currentFrameValue) as TextView?
    private val totalScoreStat:  TextView? = parentView?.findViewById(R.id.totalScoreValue) as TextView?
    private val maxPossibleStat: TextView? = parentView?.findViewById(R.id.maxPossibleValue) as TextView?

    init {
        setDefaultValues()
        registerListeners()
    }

    override fun setDefaultValues() {
        parentView?.visibility = if (controller.showStats) {
            View.VISIBLE
        } else {
            View.GONE
        }

        currentFrameStat?.text = toString(DEFAULT_START_INDEX + 1)
    }

    override fun registerListeners() {}

    override fun assignInteraction(view: View?) {}

    fun setFrameValue(frameIndex: Int) {
        currentFrameStat?.text = frameIndex.toString()
    }

    fun setTotalScore(score: Int) {
        totalScoreStat?.text = score.toString()
    }

    fun setMaxPossibleScore(score: Int) {
        maxPossibleStat?.text = score.toString()
    }
}