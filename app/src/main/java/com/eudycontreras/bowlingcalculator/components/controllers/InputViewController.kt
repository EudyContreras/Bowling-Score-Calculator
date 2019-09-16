package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.InputViewComponent

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class InputViewController(
    context: MainActivity,
    private val scoreController: ScoreController
) {
    private var viewComponent: InputViewComponent = InputViewComponent(context, this)

    internal var concealDelayMini: Long = 500
    internal var concealDelay: Long = 4000

    @Volatile internal var revealed: Boolean = false

    init {
        scoreController.inputNameController = this
    }

    fun saveNewName(nameInfo: InputViewComponent.RenameInfo, onSaved: (name: String)-> Unit) {
        scoreController.saveBowlerName(nameInfo.bowlerId, nameInfo.newName, onSaved)
    }

    fun requestRename(bowlerId: Long, bowlerName: String) {
        viewComponent.requestRename(bowlerId, bowlerName)
    }
}