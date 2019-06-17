package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.adapters.TabViewAdapter
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.controllers.TabsViewController
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by eudycontreras.
 */

class TabsViewComponent(
    private val context: MainActivity,
    val controller: TabsViewController
) : ViewComponent {

    private lateinit var tabAdapter: TabViewAdapter

    private val tabRecycler: RecyclerView? = context.tabsArea as RecyclerView

    private var smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return LinearSmoothScroller.SNAP_TO_START
        }
    }

    init {
        setDefaultValues()
    }

    override fun setDefaultValues() {}

    override fun registerListeners() {}

    override fun assignInteraction(view: View?) { }

    fun scrollToIndex(index: Int) {
        val layoutManager = tabRecycler?.layoutManager
        layoutManager?.let {
            smoothScroller.targetPosition = index
            layoutManager.startSmoothScroll(smoothScroller)
        }
    }

    fun crateTabs(bowlers: List<Bowler>) {
        val list = ArrayList<TabViewAdapter.TabViewModel>()

        bowlers.forEachIndexed { index, bowler ->
            list.add(TabViewAdapter.TabViewModel.fromBowler(bowler, index))
        }

        list.add(
            TabViewAdapter.TabViewModel(index = list.size).also { it.type = TabViewAdapter.ViewType.ADD_TAB }
        )
        tabRecycler?.let {
            tabAdapter = TabViewAdapter(context, this, list)
            it.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            it.adapter = tabAdapter
        }

    }

    fun createTab() {
        controller.addTab()
    }

    fun addTabs(bowlers: List<Bowler>) {
        if (bowlers.size == 1){
            addTab(bowlers.first())
            return
        }
        val start = tabAdapter.itemCount - 1
        val end = start + (bowlers.size - 1)

        tabAdapter.lastTab?.let { reference ->
            if (!reference.isEnqueued) {
                (reference.get() as TabViewAdapter.TabViewHolderNormal).deactivateTab()
            }
        }
        bowlers.forEach {
            val model = TabViewAdapter.TabViewModel(
                bowlerName = it.name,
                bowlerId = it.id,
                index = tabAdapter.currentIndex
            )
            tabAdapter.addItem(tabAdapter.currentIndex, model)
        }

        tabAdapter.currentIndex = end
        tabAdapter.lastSelected = end
        tabAdapter.lastTab = null
        tabAdapter.notifyDataSetChanged()
        scrollToIndex(end)
    }

    fun addTab(vararg bowler: Bowler) {
        addTabs(bowler.toList())
    }

    fun removeTab(index: Int, onEnd: (() -> Unit)? = null) {
        onEnd?.invoke()
    }

    fun selectTab(index: Int) {
        tabAdapter.currentIndex = index
        tabAdapter.notifyDataSetChanged()
        scrollToIndex(index)
    }
}