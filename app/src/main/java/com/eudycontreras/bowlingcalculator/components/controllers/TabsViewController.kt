package com.eudycontreras.bowlingcalculator.components.controllers

import android.view.View
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.adapters.TabViewAdapter
import com.eudycontreras.bowlingcalculator.calculator.controllers.ScoreController
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.views.CreateViewComponent
import com.eudycontreras.bowlingcalculator.components.views.EmptyStateViewComponent
import com.eudycontreras.bowlingcalculator.components.views.TabsViewComponent
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.listeners.PaletteListener
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.app
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette
import kotlinx.android.synthetic.main.dialog_create_bowlers.view.*

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class TabsViewController(
    val context: MainActivity,
    private val scoreController: ScoreController
): PaletteListener {

    private var viewComponent: TabsViewComponent = TabsViewComponent(context, this)
    private var createComponent: CreateViewComponent = CreateViewComponent(context, this)

    init {
        scoreController.tabsController = this
        val onDismiss = {
            if (!context.app.persistenceManager.hasBowlers()) {
                val state = EmptyStateViewComponent.EmptyState.Main(context) {
                    onTabRequested(view = it)
                }
                scoreController.emptyStateController.setState(state)
                scoreController.emptyStateController.revealState()
            } else {
                scoreController.emptyStateController.concealState()
            }
        }
        createComponent.setOptions(true, null, onDismiss)
    }

    fun hideDialogIcon(hide: Boolean = true) {
        createComponent.parentView.windowIcon.alpha = if (hide) 0f else 1f
    }

    fun onTabRequested(fromEmptyState: Boolean = false, view: View? = null) {
        view?.let {
            if (fromEmptyState) {
                hideDialogIcon(false)
            }
            context.morphTransitioner.startView = it as MorphLayout
            context.showOverlay()
            createComponent.show(fromEmptyState = fromEmptyState)
        }
    }

    fun requestTabRemoval(lastIndex: Int, index: Int, onEnd: (()-> Unit)? = null) {
        scoreController.removeBowler(lastIndex, index, onEnd)
    }

    fun onTabSelection(current: Int, manual: Boolean = false) {
        scoreController.selectBowler(current, manual)
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

    override fun onNewPalette(palette: Palette) {
        createComponent.onNewPalette(palette)
    }
}