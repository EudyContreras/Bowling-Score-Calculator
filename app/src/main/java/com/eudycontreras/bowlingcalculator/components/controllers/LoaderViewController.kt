package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.LoaderViewComponent

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class LoaderViewController(
    context: MainActivity,
    private val scoreController: ScoreController
) {

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
}