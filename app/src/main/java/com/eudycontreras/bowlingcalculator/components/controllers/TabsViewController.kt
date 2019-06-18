package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.views.TabsViewComponent
import com.eudycontreras.bowlingcalculator.extensions.app
import com.eudycontreras.bowlingcalculator.fragments.CreateBowlerFragment

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

    fun createTabs(vararg bowler: Bowler) {
        viewComponent.crateTabs(bowler.toList())
    }

    fun createTabs(bowler: List<Bowler>) {
        viewComponent.crateTabs(bowler)
    }

    fun addTab() {
        context.openDialog(CreateBowlerFragment.instance(this))
    }

    fun removeTab(index: Int) {
        viewComponent.removeTab(index)
    }

    fun selectTab(index: Int) {
        viewComponent.selectTab(index)
    }

    fun performTabSelection(bowlerId: Long, current: Int) {
        scoreController.selectBowler(bowlerId)
    }

    fun createBowler(names: List<String>, listener: (names: List<String>) -> Unit) {
        val bowlers = names.map { Bowler(it) }

        context.app.saveBowlers(bowlers) {
            listener.invoke(it.map { bowler -> bowler.name })
            viewComponent.addTabs(it)
        }
    }
}