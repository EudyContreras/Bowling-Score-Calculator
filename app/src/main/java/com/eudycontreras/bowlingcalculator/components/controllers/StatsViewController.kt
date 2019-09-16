package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.StatsViewComponent

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
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