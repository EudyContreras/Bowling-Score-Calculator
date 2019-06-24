package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.views.SkeletonViewComponent
import com.eudycontreras.bowlingcalculator.components.views.TabsViewComponent
import com.eudycontreras.bowlingcalculator.fragments.FragmentCreateBowler
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.app

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

    fun createTabs(bowler: List<Bowler>) {
        viewComponent.crateTabs(bowler)
    }

    fun requestTab(manual: Boolean, listener: BowlerListener = null) {
        val onDismiss = {
            if (!context.app.persistenceManager.hasBowlers()) {
                scoreController.skeletonController.setState(SkeletonViewComponent.EmptyState.Default(context) { requestTab(true) })
                scoreController.skeletonController.revealState()
            } else {
                scoreController.skeletonController.concealState()
            }
        }
        context.openDialog(FragmentCreateBowler.instance(this, manual, listener, onDismiss))
    }

    fun addTabs(bowlers: List<Bowler>, currentIndex: Int? = null, manual: Boolean) {
        if(bowlers.isNotEmpty())
        viewComponent.addTabs(bowlers, currentIndex, manual)
    }

    fun removeTab(lastIndex: Int, index: Int, onEnd: (()-> Unit)? = null) {
        scoreController.removeBowler(lastIndex, index, onEnd)
    }

    fun selectTab(index: Int) {
        viewComponent.selectTab(index)
    }

    fun onTabSelection(current: Int, manual: Boolean = false) {
        scoreController.selectBowler(current, manual)
    }

    fun createBowler(names: List<String>, manual: Boolean, listener: BowlerListener) {
        val bowlers = names.map { Bowler(it) }

        context.saveCurrentState(bowlers) {
            listener?.invoke(it)
            bowlers.forEach { bowler ->
                if (!scoreController.bowlers.contains(bowler)) {
                    scoreController.bowlers.add(bowler)
                }
            }
            if (!viewComponent.hasTabs()) {
                val activeTab = getActive()
                scoreController.initCalculator(it, activeTab)
                context.app.persistenceManager.saveActiveTab(activeTab)
            }

            viewComponent.addTabs(it,null, manual)
        }
    }

    fun getActive(): Int {
        return viewComponent.getCurrent()
    }
}