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
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class ActionViewController(
    private val context: MainActivity,
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
        context.openDialog(FragmentSaveResult.instance(this))
    }

    fun handleLoadAction() {
        context.openDialog(FragmentLoadResult.instance(this))
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

    fun saveCurrentResult(name: String, listener: (name: String) -> Unit) {
        val result = Result(name, Date())
        context.app.persistenceManager.saveResult(result, listener)
    }
}