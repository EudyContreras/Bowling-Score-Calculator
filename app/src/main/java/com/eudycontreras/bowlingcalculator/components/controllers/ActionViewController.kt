package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.ActionsViewComponent


/**
 * Created by eudycontreras.
 */
class ActionViewController(
    context: MainActivity,
    private val scoreController: ScoreController
) {

    private var viewComponent: ActionsViewComponent = ActionsViewComponent(context, this)

    init {
         scoreController.actionController = this
    }

    fun handleResetAction() {
        scoreController.clearScore()
    }

    fun handleThrowActions(pinCount: Int) {
        scoreController.throwBall(pinCount)
    }

    fun handleSaveAction() {

    }

    fun handleLoadAction() {

    }

    fun updateActionInput(remainingPins: Int) {
        viewComponent.setAvailablePins(remainingPins)
    }

    fun deactivateAllInput() {
        viewComponent.setAvailablePins(-1)
    }

    fun revealPins() {
        viewComponent.revealAvailablePins()
    }
}