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
import com.eudycontreras.bowlingcalculator.extensions.app
import com.eudycontreras.bowlingcalculator.extensions.getComputedScore


/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since June 21 2019
 */

class ScoreController(private val activity: MainActivity) : ScoreStateListener, BowlerActionListener{

    lateinit var actionController: ActionViewController
    lateinit var framesController: FramesViewController
    lateinit var statsController: StatsViewController
    lateinit var tabsController: TabsViewController

    var bowlers: ArrayList<Bowler> = ArrayList()

    var activeTab: Int = DEFAULT_START_INDEX

    val bowler: Bowler
        get() = bowlers[activeTab]

    fun initCalculator(bowlers: List<Bowler>, activeTab: Int) {
        this.bowlers = ArrayList(bowlers)
        this.activeTab = activeTab

        if(bowlers.isEmpty())
            return

        framesController.createFrames(bowler)
        framesController.setSourceFrames(bowler)
        actionController.revealPins()
        framesController.revealFrames()

        onScoreUpdated(
            bowler,
            bowler.frames.getComputedScore(),
            MAX_POSSIBLE_SCORE_GAME
        )
    }

    private fun allowRedoChance(frame: Frame, chance: Frame.State) {
        bowler.currentFrameIndex = frame.index
        frame.state = chance
        frame.resetChances()
        frame.resetPins()
    }

    override fun throwBall(pinKnockedCount: Int) {
        if (bowlers.isEmpty())
            return

        bowler.performRoll(pinKnockedCount, this)
        activity.app.persistenceManager.updateBowler(bowler)
    }

    override fun clearScore() {
        if (bowlers.isEmpty())
            return

        bowler.reset()
        statsController.updateTotalScore(0)
        statsController.updateMaxPossibleScore(MAX_POSSIBLE_SCORE_GAME)
        statsController.setCurrentFrame(bowler.currentFrameIndex + 1)
        actionController.updateActionInput(DEFAULT_PIN_COUNT)
        framesController.resetFrames()

        activity.app.persistenceManager.resetBowler(bowler)
    }

    override fun onFrameSelected(frameIndex: Int) {
        bowler.currentFrameIndex = frameIndex
        statsController.setCurrentFrame(frameIndex + 1)
        actionController.updateActionInput(bowler.getCurrentFrame().pinUpCount())
        allowRedoChance(bowler.getCurrentFrame(), Frame.State.FIRST_CHANCE)
    }

    override fun onScoreUpdated(bowler: Bowler, totalScore: Int, totalPossible: Int) {
        val current: Frame = bowler.getCurrentFrame()

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

    fun selectBowler(tabIndex: Int) {
        activeTab = tabIndex
        activity.app.persistenceManager.saveActiveTab(activeTab)

        val bowler: Bowler? = if (bowlers.isEmpty()) null else bowler

        framesController.setSourceFrames(bowler)

        if (bowler != null) {

            onScoreUpdated(
                bowler,
                bowler.frames.getComputedScore(),
                MAX_POSSIBLE_SCORE_GAME
            )
        } else {
            statsController.updateTotalScore(0)
            statsController.updateMaxPossibleScore(MAX_POSSIBLE_SCORE_GAME)
            statsController.setCurrentFrame(DEFAULT_START_INDEX + 1)
            actionController.deactivateAllInput()
        }
    }

    fun removeBowler(lastIndex: Int, index: Int, onEnd: (() -> Unit)?) {
        activity.app.persistenceManager.saveActiveTab(index)
        activeTab = index

        val bowler = bowlers.removeAt(lastIndex)

        activity.app.persistenceManager.removeBowler(bowler) {
            onEnd?.invoke()
            selectBowler(index)
        }
    }
}