package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.InputViewComponent

class InputViewController(
    context: MainActivity,
    private val scoreController: ScoreController
) {
    private var viewComponent: InputViewComponent = InputViewComponent(context, this)

    internal var concealDelay: Long = 4000

    init {
        scoreController.inputNameController = this
    }

    fun updateNameChange(newName: String, oldName: String) {

    }

    fun saveNewName(newName: String, oldName: String, onSaved: (name: String)-> Unit) {

    }

    fun requestRename(bowlerId: Long, bowlerName: String) {
        viewComponent.requestRename(bowlerId, bowlerName)
    }
}