package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.components.views.FramesViewComponent

/**
 * Created by eudycontreras.
 */

class FramesViewController(
    context: MainActivity,
    private val scoreController: ScoreController
) {

    private var viewComponent: FramesViewComponent = FramesViewComponent(context, this)

    init {
        scoreController.framesController = this
    }

    fun canSelect(wantedIndex: Int, lastIndex: Int? = null): Boolean {
        val canProceed = scoreController.bowler.lastPlayedFrameIndex >= wantedIndex

        if (lastIndex != null) {
            val frame: Frame = scoreController.bowler.frames[lastIndex]

            val inProgress = frame.inProgress

            if (inProgress && frame.missingRounds()) {
                return false
            }
            return canProceed
        }
        return canProceed
    }

    fun createFrames(bowler: Bowler){
        viewComponent.createFrames(bowler)
    }

    fun updateFramesState(bowler: Bowler, current: Frame) {
        viewComponent.updateFrames(bowler, current)
    }

    fun resetFrames() {
        viewComponent.resetFrames()
    }

    fun revealFrames() {
        viewComponent.revealFrames()
    }

    fun performFrameSelection(index: Int) {
        scoreController.onFrameSelected(index)
    }
}