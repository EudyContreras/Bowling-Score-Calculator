package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.components.views.ActionsViewComponent
import com.eudycontreras.bowlingcalculator.fragments.FragmentLoadResult
import com.eudycontreras.bowlingcalculator.fragments.FragmentSaveResult
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import java.util.*


/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class ActionViewController(
    private val context: MainActivity,
    private val scoreController: ScoreController
) {

    private var viewComponent: ActionsViewComponent = ActionsViewComponent(context, this)

    init {
         scoreController.actionController = this
    }

    fun performReset() {
        scoreController.clearScore()
    }

    fun performThrow(pinCount: Int) {
        scoreController.throwBall(pinCount)
    }

    fun saveResult() {
        context.openDialog(FragmentSaveResult.instance(this))
    }

    fun loadResult() {
        context.openDialog(FragmentLoadResult.instance(this))
    }

    fun updateActionInput(remainingPins: Int, duration: Long = 400) {
        viewComponent.setAvailablePins(remainingPins, duration)
    }

    fun deactivateAllInput() {
        viewComponent.setAvailablePins(-1, 450)
    }

    fun revealPins() {
        viewComponent.revealAvailablePins()
    }

    fun saveCurrentResult(name: String, listener: (name: String) -> Unit) {
        val result = Result(name, Date())
        context.app.persistenceManager.saveResult(result, listener)
    }
}