package com.eudycontreras.bowlingcalculator.components.controllers

import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.adapters.TabViewAdapter
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.views.EmptyStateViewComponent
import com.eudycontreras.bowlingcalculator.components.views.TabsViewComponent
import com.eudycontreras.bowlingcalculator.libraries.morpher.MorphTransitioner
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import kotlinx.android.synthetic.main.activity_main.*

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


    fun onTabRequested(manual: Boolean, shape: View, listener: BowlerListener = null) {
        val onDismiss = {
            if (!context.app.persistenceManager.hasBowlers()) {
                scoreController.emptyStateController.setState(EmptyStateViewComponent.EmptyState.Main(context) {
                    onTabRequested(true, shape)
                })
                scoreController.emptyStateController.revealState()
            } else {
                scoreController.emptyStateController.concealState()
            }
        }
        val transition = MorphTransitioner(context.tab as MorphLayout, context.dialog as MorphLayout)
        transition.animateTo(1f, 400, 400, interpolator =  FastOutSlowInInterpolator())

/*        context.morphingUtility.setSourceView(context.tab)
        context. morphingUtility.setDialogView(context.dialog as ViewGroup)

        doWith(shape.backgroundTintList, context.dialog.backgroundTintList) { colorFrom, colorTo ->
            context.morphingUtility.colorFrom = colorFrom.defaultColor
            context.morphingUtility.colorTo = colorTo.defaultColor
        }

        context.morphingUtility.morphIntoDialog(300, null, null)*/
        //context.openDialog(FragmentCreateBowler.instance(this, manual, listener, onDismiss))
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