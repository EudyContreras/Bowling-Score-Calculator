package com.eudycontreras.bowlingcalculator.adapters

import android.app.Activity
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.views.TabsViewComponent
import com.eudycontreras.bowlingcalculator.listeners.AnimationListener
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.extensions.attach
import com.eudycontreras.bowlingcalculator.utilities.extensions.detach
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import kotlinx.android.synthetic.main.item_tab_view.view.*
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

class TabViewAdapter(
    private val context: Activity,
    private val viewComponent: TabsViewComponent,
    private var items: ArrayList<TabViewModel>
) : RecyclerView.Adapter<TabViewAdapter.TabViewHolder>() {

    internal var lastTab: WeakReference<TabViewHolder>? = null

    internal var currentIndex: Int = DEFAULT_START_INDEX

    val normalTabs: List<TabViewModel>
        get() = items.filter { it.type == ViewType.NORMAL_TAB }

    sealed class Values {
        companion object {
            const val alpha = 0.6f
            const val changeDuration = 400L
            const val removeDuration = 200L
            val translateY = 4.dp
            val translateZ = (-10).dp
        }
    }

    fun updateItem(id: Long, name: String) {
        val index = this.items.indexOfFirst { it.bowlerId == id }
        items[index].bowlerName = name
        notifyDataSetChanged()
    }

    fun addItem(item: TabViewModel, selectedIndex: Int? = null, manual: Boolean = false) {
        lastTab?.let { reference ->
            if (!reference.isEnqueued) {
                (reference.get() as TabViewHolderNormal?)?.deactivateTab()
            }
        }

        val isManual = manual && items.size > 1

        this.items.add(currentIndex, item)

        currentIndex = selectedIndex?:this.items.size - 2
        lastTab = null

        notifyItemInserted(currentIndex)
        viewComponent.controller.onTabSelection(currentIndex, isManual)
    }

    fun addItems(items: List<TabViewModel>, selectedIndex: Int? = null, manual: Boolean = false) {
        if (items.isEmpty())
            return

        lastTab?.let { reference ->
            if (!reference.isEnqueued) {
                (reference.get() as TabViewHolderNormal?)?.deactivateTab()
            }
        }

        val isManual = manual && this.items.size > 1

        items.asReversed().forEach {
            this.items.add(currentIndex, it)
            notifyItemInserted(currentIndex)
            if (currentIndex > 0) {
                notifyItemChanged(currentIndex - 1)
            }
        }

        currentIndex = selectedIndex?:this.items.size - 2
        lastTab = null

        viewComponent.scrollToIndex(currentIndex)
        viewComponent.controller.onTabSelection(currentIndex, isManual)
    }

    fun removeItem(index: Int) {
        items.removeAt(index)
    }

    override fun getItemViewType(position: Int): Int {
        val type = items[position].type
        return when (type) {
            ViewType.ADD_TAB -> ViewType.ADD_TAB.value
            ViewType.NORMAL_TAB -> ViewType.NORMAL_TAB.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        return when(viewType) {
            ViewType.ADD_TAB.value -> {
                TabViewHolderAdd(LayoutInflater.from(context).inflate(R.layout.item_tab_view_add, parent, false))
            }
            else -> {
                TabViewHolderNormal(LayoutInflater.from(context).inflate(R.layout.item_tab_view, parent, false))
            }
        }
    }

    override fun onViewRecycled(holder: TabViewHolder) {
        super.onViewRecycled(holder)
        holder.resetValues()
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.performBinding(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].bowlerId
    }

    abstract class TabViewHolder(view: View) : RecyclerView.ViewHolder(view){
        abstract fun resetValues()
        abstract fun registerListeners()
        abstract fun performBinding(model: TabViewModel)
    }

    inner class TabViewHolderAdd(view: View) : TabViewHolder(view) {

        private var model: TabViewModel? = null

        init {
            resetValues()
            registerListeners()
        }

        override fun registerListeners() {
            itemView.setOnClickListener {
                model?.let {
                    currentIndex = layoutPosition
                    viewComponent.controller.hideDialogIcon()
                    viewComponent.controller.onTabRequested(view = itemView)
                }
            }
        }

        override fun resetValues() { }

        override fun performBinding(model: TabViewModel) {
            this.model = model
        }
    }

    inner class TabViewHolderNormal(view: View) : TabViewHolder(view){

        private val tabItem: ConstraintLayout = view.tabItem
        private val tabLabel: TextView = tabItem.tabItemLabel

        private val tabAction: FrameLayout = view.tabItemAction

        private var model: TabViewModel? = null

        private var removed = false

        private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                viewComponent.controller.requestRename(model)
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (currentIndex == layoutPosition) {
                    return false
                }
                currentIndex = layoutPosition
                viewComponent.controller.onTabSelection(layoutPosition, true)
                lastTab?.let { reference ->
                    if (!reference.isEnqueued) {
                        (reference.get() as TabViewHolderNormal?)?.deactivateTab()
                    }
                }
                activateTab()
                return super.onSingleTapUp(e)
            }
        })

        init {
            resetValues()
            registerListeners()
        }

        override fun registerListeners(){
            this.tabItem.addTouchAnimation(
                clickTarget = null,
                depth = (-8).dp,
                interpolatorPress = DecelerateInterpolator(),
                interpolatorRelease = OvershootInterpolator(),
                gestureDetector = gestureDetector
            )

            this.tabAction.setOnClickListener {
                removeTab()
            }
        }

        override fun resetValues() {
            this.removed = false
            this.tabAction.detach()
            this.tabItem.alpha = Values.alpha
            this.tabItem.translationY = Values.translateY
            this.tabItem.translationZ = Values.translateZ
        }

        override fun performBinding(model: TabViewModel) {
            this.model = model

            tabLabel.text = model.bowlerName

            if (layoutPosition == currentIndex) {
                activateTab()
            }
        }

        private fun activateTab() {
            lastTab = WeakReference(this)
            this.tabAction.attach()
            this.tabItem.animate()
                .setListener(null)
                .alpha(1f)
                .translationY(0f)
                .translationZ(0f)
                .setDuration(Values.changeDuration)
                .start()
        }

        fun deactivateTab() {
            this.tabAction.detach()
            this.tabItem.animate()
                .setListener(null)
                .alpha(Values.alpha)
                .translationY(Values.translateY)
                .translationZ(Values.translateZ)
                .setDuration(Values.changeDuration)
                .start()
        }

        private fun animateRemoval(onEnd: ()-> Unit) {
            this.tabItem.animate()
                .alpha(0f)
                .translationZ(Values.translateZ)
                .setDuration(Values.removeDuration)
                .setListener(AnimationListener(onEnd = onEnd))
                .start()
        }

        private fun removeTab() {
            val lastTabIndex = layoutPosition
            removed = true
            currentIndex = if (layoutPosition == (itemCount - 2) && layoutPosition != 0) {
                layoutPosition - 1
            } else {
                layoutPosition
            }
            val onRemoved = {
                animateRemoval {
                    removeItem(layoutPosition)
                    notifyItemRemoved(layoutPosition)
                    notifyItemChanged(currentIndex)
                }
            }
            viewComponent.controller.requestTabRemoval(lastTabIndex, currentIndex, onRemoved)
        }
    }

    enum class ViewType(val value: Int) {
        ADD_TAB(0),
        NORMAL_TAB(1);
    }

    data class TabViewModel(
        val bowlerId: Long = 0,
        var bowlerName: String = ""
    ) {
        var type: ViewType = ViewType.NORMAL_TAB

        companion object {
            fun fromBowler(bowler: Bowler) : TabViewModel {
                return TabViewModel(
                    bowlerId = bowler.id,
                    bowlerName = bowler.name
                )
            }
        }
    }
}
