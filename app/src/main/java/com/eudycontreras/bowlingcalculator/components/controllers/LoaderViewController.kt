package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.LoaderViewComponent
import com.eudycontreras.bowlingcalculator.listeners.PaletteListener
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class LoaderViewController(
    context: MainActivity,
    scoreController: ScoreController
): PaletteListener {

    private var viewComponent: LoaderViewComponent = LoaderViewComponent(context, this)

    init {
        scoreController.loaderController = this
    }

    fun showLoader() {
        viewComponent.showLoader()
    }

    fun hideLoader() {
        viewComponent.hideLoader()
    }

    override fun onNewPalette(palette: Palette) {
        viewComponent.onNewPalette(palette)
    }
}