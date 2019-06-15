package com.eudycontreras.bowlingcalculator.calculator.controllers

import com.eudycontreras.bowlingcalculator.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.MAX_POSSIBLE_SCORE_GAME
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast
import com.eudycontreras.bowlingcalculator.calculator.listeners.BowlerActionListener
import com.eudycontreras.bowlingcalculator.calculator.listeners.ScoreStateListener
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.components.controllers.FramesViewController
import com.eudycontreras.bowlingcalculator.components.controllers.StatsViewController


/**
 * Created by eudycontreras.
 */

class ScoreController(val bowler: Bowler) : ScoreStateListener, BowlerActionListener{

    lateinit var actionController: ActionViewController
    lateinit var framesController: FramesViewController
    lateinit var statsController: StatsViewController

    private fun allowRedoChance(frame: Frame, chance: Frame.State) {
        bowler.currentFrameIndex = frame.index
        frame.state = chance
        frame.resetChances()
        frame.resetPins()
    }

    override fun throwBall(pinKnockedCount: Int) {
        bowler.performRoll(pinKnockedCount, this)
    }

    override fun clearScore() {
        bowler.reset()
        statsController.updateTotalScore(0)
        statsController.updateMaxPossibleScore(MAX_POSSIBLE_SCORE_GAME)
        statsController.setCurrentFrame(bowler.currentFrameIndex + 1)
        actionController.updateActionInput(DEFAULT_PIN_COUNT)
        framesController.resetFrames()

    }

    override fun onFrameSelected(frameIndex: Int) {
        bowler.currentFrameIndex = frameIndex
        statsController.setCurrentFrame(frameIndex + 1)
        actionController.updateActionInput(bowler.getCurrentFrame().pinUpCount())
        allowRedoChance(bowler.getCurrentFrame(), Frame.State.FIRST_CHANCE)
    }

    override fun onScoreUpdated(bowler: Bowler, current: Frame, frames: List<Frame>, totalScore: Int, totalPossible: Int) {
        framesController.updateFramesState(bowler, current)
        statsController.updateTotalScore(totalScore)
        statsController.updateMaxPossibleScore(totalPossible)
        statsController.setCurrentFrame(current.index + 1)
        actionController.updateActionInput(current.pinUpCount())
        if (current is FrameLast) {
            if (current.isCompleted) {
                actionController.deactivateAllInput()
                return
            }
        }
    }
}