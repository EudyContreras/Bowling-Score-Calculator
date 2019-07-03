package com.eudycontreras.bowlingcalculator.components.controllers

import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.adapters.TabViewAdapter
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
        scoreController.createBowler(names, manual, listener)
    }

    fun requestRename(model: TabViewAdapter.TabViewModel?) {
        model?.let {
            scoreController.requestRename(model.bowlerId, model.bowlerName)
        }
    }

    fun updateTabName(bowlerId: Long, newName: String) {
        viewComponent.updateTabName(bowlerId, newName)
    }

    fun hasTabs(): Boolean = viewComponent.hasTabs()

    fun getActive(): Int = viewComponent.getCurrent()
}