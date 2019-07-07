package com.eudycontreras.bowlingcalculator.components.controllers

import android.view.View
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.EmptyStateViewComponent

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class EmptyStateViewController(
    context: MainActivity,
    parentView: View,
    scoreController: ScoreController? = null
) {

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
}