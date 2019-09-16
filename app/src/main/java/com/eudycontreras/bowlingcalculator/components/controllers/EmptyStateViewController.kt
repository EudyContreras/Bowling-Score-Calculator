package com.eudycontreras.bowlingcalculator.components.controllers

import android.view.View
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.EmptyStateViewComponent
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

class EmptyStateViewController(
    context: MainActivity,
    parentView: View,
    scoreController: ScoreController? = null
): PaletteListener {

    private var viewComponent: EmptyStateViewComponent = EmptyStateViewComponent(context, parentView, this)

    init {
        scoreController?.emptyStateController = this
    }

    fun concealState(onEnd: (() -> Unit)? = null) {
        viewComponent.concealState(onEnd)
    }

    fun revealState(onEnd: (() -> Unit)? = null) {
        viewComponent.revealState(onEnd)
    }

    fun setState(state: EmptyStateViewComponent.EmptyState) {
        viewComponent.setEmptyState(state)
    }

    override fun onNewPalette(palette: Palette) {
        viewComponent.onNewPalette(palette)
    }
}