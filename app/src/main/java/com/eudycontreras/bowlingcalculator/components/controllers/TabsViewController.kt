package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.views.SkeletonViewComponent
import com.eudycontreras.bowlingcalculator.components.views.TabsViewComponent
import com.eudycontreras.bowlingcalculator.fragments.FragmentCreateBowler
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.DuplicateBowlerException
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import com.eudycontreras.bowlingcalculator.utilities.extensions.string

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class TabsViewController(
    private val context: MainActivity,
    private val scoreController: ScoreController
) {

    private var viewComponent: TabsViewComponent = TabsViewComponent(context, this)

    init {
        scoreController.tabsController = this
    }


    fun onTabRequested(manual: Boolean, listener: BowlerListener = null) {
        val onDismiss = {
            if (!context.app.persistenceManager.hasBowlers()) {
                scoreController.skeletonController.setState(SkeletonViewComponent.EmptyState.Default(context) {
                    onTabRequested(true)
                })
                scoreController.skeletonController.revealState()
            } else {
                scoreController.skeletonController.concealState()
            }
        }
        context.openDialog(FragmentCreateBowler.instance(this, manual, listener, onDismiss))
    }

    fun requestTabRemoval(lastIndex: Int, index: Int, onEnd: (()-> Unit)? = null) {
        scoreController.removeBowler(lastIndex, index, onEnd)
    }

    fun onTabSelection(current: Int, manual: Boolean = false) {
        scoreController.selectBowler(current, manual)
    }

    fun selectTab(index: Int) {
        viewComponent.selectTab(index)
    }


    fun addTabs(bowlers: List<Bowler>, currentIndex: Int? = null, manual: Boolean) {
        if(bowlers.isNotEmpty())
            viewComponent.addTabs(bowlers, currentIndex, manual)
    }

    fun createTabs(bowler: List<Bowler>) {
        viewComponent.crateTabs(bowler)
    }

    fun createBowler(names: List<String>, manual: Boolean, listener: BowlerListener) {
        val bowlers = names.map { Bowler(it) }

        context.saveCurrentState(bowlers) {
            bowlers.forEach { bowler ->
                if (scoreController.bowlers.isEmpty() || !scoreController.bowlers.contains(bowler)) {
                    scoreController.bowlers.add(bowler)
                } else {
                    throw DuplicateBowlerException(context.string(R.string.exception_bowler_exists))
                }
            }
            if (!viewComponent.hasTabs()) {
                val activeTab = getActive()
                scoreController.initCalculator(it, activeTab)
                context.app.persistenceManager.saveActiveTab(activeTab)
            }

            listener?.invoke(it)
            viewComponent.addTabs(it, null, manual)
        }
    }

    private fun getActive(): Int {
        return viewComponent.getCurrent()
    }
}