package com.eudycontreras.bowlingcalculator.calculator.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast
import com.eudycontreras.bowlingcalculator.calculator.listeners.BowlerActionListener
import com.eudycontreras.bowlingcalculator.calculator.listeners.ScoreStateListener
import com.eudycontreras.bowlingcalculator.components.controllers.*
import com.eudycontreras.bowlingcalculator.components.views.SkeletonViewComponent
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.utilities.MAX_POSSIBLE_SCORE_GAME
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import com.eudycontreras.bowlingcalculator.utilities.extensions.getComputedScore
import com.eudycontreras.bowlingcalculator.utilities.extensions.getPossibleScore
import com.eudycontreras.bowlingcalculator.utilities.runAfterMain


/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class ScoreController(private val activity: MainActivity) : ScoreStateListener, BowlerActionListener{

    lateinit var loaderController: LoaderViewController
    lateinit var skeletonController: SkeletonViewController
    lateinit var actionController: ActionViewController
    lateinit var framesController: FramesViewController
    lateinit var statsController: StatsViewController
    lateinit var tabsController: TabsViewController

    var bowlers: ArrayList<Bowler> = ArrayList()

    var activeTab: Int = DEFAULT_START_INDEX

    val bowler: Bowler
        get() = bowlers[activeTab]

    fun initCalculator(bowlers: List<Bowler>, activeTab: Int, newBowler: Boolean = false) {
        this.bowlers = ArrayList(bowlers)
        this.activeTab = activeTab

        if(bowlers.isEmpty())
            return

        framesController.createFrames(bowler)

        if (!bowler.hasStarted()) {
            actionController.revealPins()
        }

        framesController.revealFrames()
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
        actionController.updateActionInput(DEFAULT_PIN_COUNT, 300)
        framesController.resetFrames()

        activity.app.persistenceManager.resetBowler(bowler)
    }

    override fun onFrameSelected(frameIndex: Int) {
        bowler.currentFrameIndex = frameIndex
        statsController.setCurrentFrame(frameIndex + 1)
        actionController.updateActionInput(bowler.getCurrentFrame().pinUpCount(), 300)
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

    fun selectBowler(tabIndex: Int, manual: Boolean) {
        activeTab = tabIndex
        activity.app.persistenceManager.saveActiveTab(activeTab)

        val bowler: Bowler? = if (bowlers.isEmpty()) null else bowler

        framesController.setSourceFrames(bowler)

        if (bowler != null) {
            if (bowler.hasStarted() || manual) {
                onScoreUpdated(
                    bowler,
                    bowler.getComputedScore(),
                    bowler.getPossibleScore()
                )
            }
        } else {
            skeletonController.setState(SkeletonViewComponent.EmptyState.Default(activity) {
                tabsController.requestTab(true)
            })
            runAfterMain(250) {
                skeletonController.revealState()
            }

            statsController.updateTotalScore(0)
            statsController.updateMaxPossibleScore(MAX_POSSIBLE_SCORE_GAME)
            statsController.setCurrentFrame(DEFAULT_START_INDEX + 1)
            actionController.deactivateAllInput()
        }
    }

    fun removeBowler(lastIndex: Int, index: Int, onEnd: (() -> Unit)?) {
        activity.app.persistenceManager.saveActiveTab(index)
        activeTab = index

        //TODO("find out what the error is")

        val bowler = bowlers.removeAt(lastIndex)

        activity.app.persistenceManager.removeBowler(bowler) {
            onEnd?.invoke()
            selectBowler(index, true)
        }
    }
}