package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.ThemeSelectViewComponent
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class PaletteViewController(
    val context: MainActivity,
    scoreController: ScoreController
) {

    private var viewComponent: ThemeSelectViewComponent = ThemeSelectViewComponent(context, this)

    init {
        scoreController.paletteController = this
    }

    fun show(duration: Long) {
       viewComponent.show(duration)
    }

    fun dismiss(duration: Long) {
        viewComponent.dismiss(duration)
    }

    fun applyThemeWith(color: Int) {
        val palette = Palette.of(color)
        context.onNewPalette(palette)
        context.morphTransitioner.startingState.color = color
        context.app.persistenceManager.updateThemeColor(color)
        dismiss(Morpher.DEFAULT_DURATION)
    }
}