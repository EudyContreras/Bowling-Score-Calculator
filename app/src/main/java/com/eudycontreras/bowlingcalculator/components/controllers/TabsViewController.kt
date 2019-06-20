package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.BowlerListener
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.views.TabsViewComponent
import com.eudycontreras.bowlingcalculator.extensions.app
import com.eudycontreras.bowlingcalculator.fragments.FragmentCreateBowler

/**
 * Created by eudycontreras.
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

    fun requestTab(listener: BowlerListener = null) {
        context.openDialog(FragmentCreateBowler.instance(this, listener))
    }

    fun addTabs(bowlers: List<Bowler>, currentIndex: Int? = null) {
        if(bowlers.isNotEmpty())
        viewComponent.addTabs(bowlers, currentIndex)
    }

    fun removeTab(lastIndex: Int, index: Int, onEnd: (()-> Unit)? = null) {
        scoreController.removeBowler(lastIndex, index, onEnd)
    }

    fun selectTab(index: Int) {
        viewComponent.selectTab(index)
    }

    fun onTabSelection(current: Int) {
        scoreController.selectBowler(current)
    }

    fun createBowler(names: List<String>, listener: BowlerListener) {
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
                context.app.persistenceManager.saveActiveTab(activeTab)
                scoreController.initCalculator(it, activeTab)
            }
            viewComponent.addTabs(it)
        }
    }

    fun getActive(): Int {
        return viewComponent.getCurrent()
    }
}