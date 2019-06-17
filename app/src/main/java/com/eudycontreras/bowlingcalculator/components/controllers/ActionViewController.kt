package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.components.views.ActionsViewComponent
import com.eudycontreras.bowlingcalculator.extensions.app
import com.eudycontreras.bowlingcalculator.fragments.LoadResultFragment
import com.eudycontreras.bowlingcalculator.fragments.SaveResultFragment
import java.util.*


/**
 * Created by eudycontreras.
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
        context.openDialog(SaveResultFragment.instance(this))
    }

    fun handleLoadAction() {
        context.openDialog(LoadResultFragment.instance(this))
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
        //result.bowlers = scoreController.bowler
        context.app.saveResult(result, listener)
    }
}