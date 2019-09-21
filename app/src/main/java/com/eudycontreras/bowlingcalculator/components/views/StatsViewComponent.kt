package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import android.widget.TextView
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.StatsViewController
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.utilities.MAX_POSSIBLE_SCORE_GAME
import com.eudycontreras.bowlingcalculator.utilities.toString
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class StatsViewComponent(
    context: MainActivity,
    private val controller: StatsViewController
) : ViewComponent() {

    private val parentView: View? = context.statsArea

    private val currentFrameStat: TextView? = parentView?.findViewById(R.id.currentFrameValue) as TextView?
    private val totalScoreStat:  TextView? = parentView?.findViewById(R.id.totalScoreValue) as TextView?
    private val maxPossibleStat: TextView? = parentView?.findViewById(R.id.maxPossibleValue) as TextView?

    init {
        setDefaultValues()
        registerListeners()
    }

    override fun setDefaultValues() {
        totalScoreStat?.text = "0"
        maxPossibleStat?.text = MAX_POSSIBLE_SCORE_GAME.toString()

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