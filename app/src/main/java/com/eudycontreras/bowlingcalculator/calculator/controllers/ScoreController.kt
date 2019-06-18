package com.eudycontreras.bowlingcalculator.calculator.controllers

import com.eudycontreras.bowlingcalculator.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.MAX_POSSIBLE_SCORE_GAME
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast
import com.eudycontreras.bowlingcalculator.calculator.listeners.BowlerActionListener
import com.eudycontreras.bowlingcalculator.calculator.listeners.ScoreStateListener
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.components.controllers.FramesViewController
import com.eudycontreras.bowlingcalculator.components.controllers.StatsViewController
import com.eudycontreras.bowlingcalculator.components.controllers.TabsViewController
import com.eudycontreras.bowlingcalculator.extensions.getComputedScore


/**
 * Created by eudycontreras.
 */

class ScoreController(private val mainActivity: MainActivity) : ScoreStateListener, BowlerActionListener{

    lateinit var actionController: ActionViewController
    lateinit var framesController: FramesViewController
    lateinit var statsController: StatsViewController
    lateinit var tabsController: TabsViewController

    var bowlers: List<Bowler> = ArrayList()

    var activeTab: Int = DEFAULT_START_INDEX

    val bowler: Bowler
        get() = bowlers[activeTab]

    fun initCalculator(bowlers: List<Bowler>, activeTab: Int) {
        this.bowlers = bowlers
        this.activeTab = activeTab

        framesController.createFrames(this.bowlers[activeTab])
        actionController.revealPins()
        framesController.revealFrames()

        onScoreUpdated(
            this.bowlers[activeTab],
            this.bowlers[activeTab].getCurrentFrame(),
            this.bowlers[activeTab].frames,
            this.bowlers[activeTab].frames.getComputedScore(),
            MAX_POSSIBLE_SCORE_GAME
        )
    }

    private fun allowRedoChance(frame: Frame, chance: Frame.State) {
        bowlers[activeTab].currentFrameIndex = frame.index
        frame.state = chance
        frame.resetChances()
        frame.resetPins()
    }

    override fun throwBall(pinKnockedCount: Int) {
        bowlers[activeTab].performRoll(pinKnockedCount, this)
    }

    override fun clearScore() {
        bowlers[activeTab].reset()
        statsController.updateTotalScore(0)
        statsController.updateMaxPossibleScore(MAX_POSSIBLE_SCORE_GAME)
        statsController.setCurrentFrame(bowlers[activeTab].currentFrameIndex + 1)
        actionController.updateActionInput(DEFAULT_PIN_COUNT)
        framesController.resetFrames()
    }

    override fun onFrameSelected(frameIndex: Int) {
        bowlers[activeTab].currentFrameIndex = frameIndex
        statsController.setCurrentFrame(frameIndex + 1)
        actionController.updateActionInput(bowlers[activeTab].getCurrentFrame().pinUpCount())
        allowRedoChance(bowlers[activeTab].getCurrentFrame(), Frame.State.FIRST_CHANCE)
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

    fun selectBowler(bowlerId: Long) {

    }
}