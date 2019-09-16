package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.components.views.FramesViewComponent

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class FramesViewController(
    context: MainActivity,
    private val scoreController: ScoreController
) {

    private var viewComponent: FramesViewComponent = FramesViewComponent(context, this)

    init {
        scoreController.framesController = this
    }

    fun checkCanSelectFrame(wantedIndex: Int, lastIndex: Int? = null): Boolean {
        return scoreController.canSelectFrame(wantedIndex, lastIndex)
    }

    fun createFrames(bowler: Bowler){
        val frames = bowler.frames.toMutableList()
        viewComponent.createFrames(frames)
    }

    fun framesCreated(): Boolean = viewComponent.framesCreated()

    fun updateFramesState(bowler: Bowler, current: Frame) {
        viewComponent.updateFrames(bowler, current)
    }

    fun resetFrames() {
        viewComponent.resetFrames()
    }

    fun revealFrames(bowler: Bowler) {
        viewComponent.revealFrames(bowler)
    }

    fun selectFrame(index: Int) {
        scoreController.onFrameSelected(index)
    }

    fun setSourceFrames(bowler: Bowler?, onEnd: (() -> Unit)? = null) {
        if (bowler != null) {
            viewComponent.setSourceFrames(bowler.frames)
        } else {
            viewComponent.concealFrames {
                onEnd?.invoke()
                viewComponent.setSourceFrames(emptyList())
            }
        }
    }
}