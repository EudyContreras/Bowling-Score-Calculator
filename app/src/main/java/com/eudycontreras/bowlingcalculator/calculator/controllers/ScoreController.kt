package com.eudycontreras.bowlingcalculator.calculator.controllers

import androidx.lifecycle.Observer
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast
import com.eudycontreras.bowlingcalculator.calculator.listeners.BowlerActionListener
import com.eudycontreras.bowlingcalculator.calculator.listeners.ScoreStateListener
import com.eudycontreras.bowlingcalculator.components.controllers.*
import com.eudycontreras.bowlingcalculator.components.views.EmptyStateViewComponent
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager
import com.eudycontreras.bowlingcalculator.utilities.*
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import com.eudycontreras.bowlingcalculator.utilities.extensions.getComputedScore
import com.eudycontreras.bowlingcalculator.utilities.extensions.getPossibleScore

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class ScoreController(
    private val activity: MainActivity
) : ScoreStateListener, BowlerActionListener {

    lateinit var loaderController: LoaderViewController
    lateinit var inputNameController: InputViewController
    lateinit var emptyStateController: EmptyStateViewController
    lateinit var paletteController: PaletteViewController
    lateinit var actionController: ActionViewController
    lateinit var framesController: FramesViewController
    lateinit var statsController: StatsViewController
    lateinit var tabsController: TabsViewController

    var bowlers: ArrayList<Bowler> = ArrayList()

    var activeTab: Int = DEFAULT_START_INDEX

    val bowler: Bowler
        get() = bowlers[activeTab]

    private fun initCalculator(bowlers: List<Bowler>, activeTab: Int) {
        this.bowlers = ArrayList(bowlers)
        this.activeTab = activeTab

        if(bowlers.isEmpty())
            return

        if (!framesController.framesCreated()) {
            framesController.createFrames(bowler)
        } else {
            framesController.setSourceFrames(bowler)
        }

        framesController.revealFrames(bowler)

        if (!bowler.hasStarted()) {
            actionController.revealPins()
        } else {
            val current: Frame = bowler.getCurrentFrame()
            framesController.updateFramesState(bowler, current)
            statsController.updateTotalScore(bowler.getComputedScore())
            statsController.updateMaxPossibleScore(bowler.getPossibleScore())
            statsController.setCurrentFrame(current.index + 1)
        }
    }

    fun onStorageEmpty() {
        val state = EmptyStateViewComponent.EmptyState.Main(activity) {
            tabsController.onTabRequested(fromEmptyState = true, view = it)
        }
        emptyStateController.setState(state)
        emptyStateController.revealState()
    }

    fun onStorageFull(persistenceManager: PersistenceManager) {
        val bowlers = persistenceManager.getBowlers()
        val activeTab = persistenceManager.getActiveTab()

        bowlers.observe(activity, Observer {
            if (it.isEmpty()) {
                onStorageEmpty()
                return@Observer
            }

            loaderController.hideLoader()
            initCalculator(it, activeTab)
            tabsController.addTabs(it, activeTab, false)
        })
    }

    override fun throwBall(pinKnockedCount: Int) {
        if (bowlers.isEmpty())
            return

        bowler.performRoll(pinKnockedCount, listener = this)

        activity.app.persistenceManager.updateBowler(bowler)
    }

    override fun clearScore() {
        if (bowlers.isEmpty())
            return

        bowler.reset()

        activity.app.persistenceManager.resetBowler(bowler) {
            statsController.updateTotalScore(it.getComputedScore())
            statsController.updateMaxPossibleScore(it.getPossibleScore())
            statsController.setCurrentFrame(it.currentFrameIndex + 1)
            actionController.updateActionInput(DEFAULT_PIN_COUNT, duration = STANDARD_DURATION)
            framesController.resetFrames()
        }
    }

    override fun onFrameSelected(frameIndex: Int) {
        bowler.currentFrameIndex = frameIndex
        statsController.setCurrentFrame(frameIndex + 1)
        statsController.updateMaxPossibleScore(bowler.getPossibleScore())
        actionController.updateActionInput(bowler.getCurrentFrame().pinUpCount(), duration = STANDARD_DURATION)

        bowler.getCurrentFrame().state = Frame.State.FIRST_CHANCE
        bowler.getCurrentFrame().resetChances()
        bowler.getCurrentFrame().resetPins()
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
            }
        }
    }

    fun selectBowler(tabIndex: Int, manual: Boolean) {
        saveActiveTab(tabIndex)

        val bowler: Bowler? = if (bowlers.isEmpty()) null else bowler

        if (bowler != null) {
            framesController.setSourceFrames(bowler)
            if (bowler.hasStarted() || manual) {
                onScoreUpdated(bowler, bowler.getComputedScore(), bowler.getPossibleScore())
            }
        } else {
            framesController.setSourceFrames(bowler = null) {
                runAfterMain(delay = 250) {
                    emptyStateController.setState(EmptyStateViewComponent.EmptyState.Main(activity) {
                        tabsController.hideDialogIcon(false)
                        tabsController.onTabRequested(fromEmptyState = true, view = it)
                    })
                    emptyStateController.revealState()
                }

                statsController.updateTotalScore(score = 0)
                statsController.updateMaxPossibleScore(MAX_POSSIBLE_SCORE_GAME)
                statsController.setCurrentFrame(DEFAULT_START_INDEX + 1)
                actionController.deactivateAllInput()
            }
        }
    }

    fun removeBowler(lastIndex: Int, current: Int, onEnd: (() -> Unit)?) {

        val onBowlerRemoved: ((Bowler) -> Unit)? = {
            saveActiveTab(current)

            bowlers.remove(it)
            onEnd?.invoke()
            selectBowler(current, true)
        }

        activity.app.persistenceManager.removeBowler(bowlers[lastIndex], onBowlerRemoved)
    }

    fun canSelectFrame(wantedIndex: Int, lastIndex: Int? = null): Boolean {
        val canProceed = bowler.lastPlayedFrameIndex >= wantedIndex

        if (lastIndex != null) {
            val frame: Frame = bowler.frames[lastIndex]

            val inProgress = frame.inProgress

            if (inProgress && frame.missingRounds()) {
                return false
            }
        }
        return canProceed
    }

    private fun saveActiveTab(index: Int): Int {
        activeTab = activity.app.persistenceManager.saveActiveTab(index)
        return activeTab
    }

    fun createBowler(names: List<String>, manual: Boolean, listener: ((names: List<Bowler>) -> Unit)?) {
        val newBowlers = names.map { Bowler(it) }
        activity.saveCurrentState(newBowlers) {
            bowlers.addAll(it)

            if (!tabsController.hasTabs()) {
                initCalculator(it, activeTab)
            }

            listener?.invoke(it)
            val activeTab = saveActiveTab(bowlers.size - 1)
            tabsController.addTabs(it, activeTab, manual)
        }
    }

    fun requestRename(bowlerId: Long, bowlerName: String) {
        inputNameController.requestRename(bowlerId, bowlerName)
    }

    fun saveBowlerName(bowlerId: Long, newName: String, onSaved: (name: String) -> Unit) {
        val bowler = bowlers.firstOrNull { it.id == bowlerId }

        bowler?.let {
            it.name = newName.trim()
            activity.app.persistenceManager.updateBowler(it) {
                onSaved(newName)
                tabsController.updateTabName(it.id, it.name)
            }
        }
    }
}