package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.components.views.SkeletonViewComponent


class SkeletonViewController(
    context: MainActivity,
    private val scoreController: ScoreController
) {

    private var viewComponent: SkeletonViewComponent = SkeletonViewComponent(context, this)

    init {
        scoreController.skeletonController = this
    }

    fun concealState(onEnd: (() -> Unit)? = null) {
        viewComponent.concealState(onEnd)
    }

    fun revealState(onEnd: (() -> Unit)? = null) {
        viewComponent.revealState(onEnd)
    }

    fun setState(state: SkeletonViewComponent.EmptyState) {
        viewComponent.setEmptyState(state)
    }
}