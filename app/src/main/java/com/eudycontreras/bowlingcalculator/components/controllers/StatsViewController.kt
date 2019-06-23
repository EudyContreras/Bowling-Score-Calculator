package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.StatsViewComponent

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class StatsViewController(
    context: MainActivity,
    scoreController: ScoreController
) {

    var showStats = true

    private var viewComponent: StatsViewComponent = StatsViewComponent(context, this)

    init {
        scoreController.statsController = this
    }

    fun setCurrentFrame(frameIndex: Int) {
        viewComponent.setFrameValue(frameIndex)
    }

    fun updateTotalScore(score: Int) {
        viewComponent.setTotalScore(score)
    }

    fun updateMaxPossibleScore(score: Int) {
        viewComponent.setMaxPossibleScore(score)
    }
}