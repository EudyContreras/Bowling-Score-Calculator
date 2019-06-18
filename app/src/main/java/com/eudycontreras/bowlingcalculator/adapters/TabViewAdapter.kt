package com.eudycontreras.bowlingcalculator.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.components.views.TabsViewComponent
import com.eudycontreras.bowlingcalculator.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.extensions.attach
import com.eudycontreras.bowlingcalculator.extensions.detach
import com.eudycontreras.bowlingcalculator.extensions.dp
import com.eudycontreras.bowlingcalculator.listeners.AnimationListener
import kotlinx.android.synthetic.main.item_tab_view.view.*
import java.lang.ref.WeakReference


/**
 * Created by eudycontreras.
 */

class TabViewAdapter(
    private val context: Activity,
    private val viewComponent: TabsViewComponent,
    private var items: ArrayList<TabViewModel>
) : RecyclerView.Adapter<TabViewAdapter.TabViewHolder>() {

    internal var lastTab: WeakReference<TabViewHolder>? = null

    internal var currentIndex: Int = DEFAULT_START_INDEX

    fun addItem(item: TabViewModel) {
        lastTab?.let { reference ->
            if (!reference.isEnqueued) {
                (reference.get() as TabViewAdapter.TabViewHolderNormal?)?.deactivateTab()
            }
        }
        this.items.add(currentIndex, item)
        notifyItemInserted(currentIndex)

        currentIndex = this.items.size - 2
        lastTab = null
    }

    fun addItems(items: List<TabViewModel>) {

        lastTab?.let { reference ->
            if (!reference.isEnqueued) {
                (reference.get() as TabViewAdapter.TabViewHolderNormal?)?.deactivateTab()
            }
        }

        items.forEach {
            this.items.add(currentIndex, it)
        }

        currentIndex = this.items.size - 2
        lastTab = null

        notifyDataSetChanged()
        viewComponent.scrollToIndex(this.items.size - 1)
    }

    fun removeItem(index: Int) {
        items.removeAt(index)
    }

    fun removeItem(item: TabViewModel) {
        items.remove(item)
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

    abstract class TabViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
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
            itemView.setOnClickListener(this)
        }

        override fun resetValues() { }

        override fun performBinding(model: TabViewModel) {
            this.model = model
            this.itemView.addTouchAnimation(
                clickTarget = null,
                scale = 0.90f,
                depth = (-8).dp,
                interpolatorPress = DecelerateInterpolator(),
                interpolatorRelease = OvershootInterpolator()
            )
        }

        override fun onClick(view: View?) {
            model?.let {
                currentIndex = layoutPosition
                viewComponent.controller.requestTab()
            }
        }
    }

    inner class TabViewHolderNormal(view: View) : TabViewHolder(view){

        private val tabItem: ConstraintLayout = view.tabItem
        private val tabLabel: TextView = tabItem.tabItemLabel

        private val tabAction: FrameLayout = view.tabItemAction

        private var model: TabViewModel? = null

        init {
            resetValues()
            registerListeners()
        }

        override fun registerListeners(){
            tabItem.setOnClickListener(this)

            tabAction.setOnClickListener {
                removeTab()
            }
        }

        override fun onClick(view: View?) {
            model?.let { model ->
                if (currentIndex != layoutPosition) {
                    currentIndex = layoutPosition
                }
                viewComponent.controller.onTabSelection(model.bowlerId, layoutPosition)
                lastTab?.let { reference ->
                    if (!reference.isEnqueued) {
                        (reference.get() as TabViewHolderNormal?)?.deactivateTab()
                    }
                }
                activateTab()
            }
        }

        override fun resetValues() {
            this.tabAction.detach()
            this.tabItem.alpha = 0.5f
            this.tabItem.translationY = 4.dp
            this.tabItem.translationZ = (-10).dp
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
                .alpha(1f)
                .setListener(null)
                .translationY(0f)
                .translationZ(0.dp)
                .setDuration(150)
                .start()
        }

        fun deactivateTab() {
            this.tabAction.detach()
            this.tabItem.animate()
                .alpha(0.5f)
                .setListener(null)
                .translationY(4.dp)
                .translationZ((-10).dp)
                .setDuration(150)
                .start()
        }

        private fun animateRemoval(onEnd: ()-> Unit) {
            this.tabItem.animate()
                .alpha(0f)
                .translationZ((-10).dp)
                .setDuration(100)
                .setListener(AnimationListener(onEnd = onEnd))
                .start()
        }

        private fun removeTab() {
            viewComponent.removeTab(layoutPosition) {
                currentIndex = if (layoutPosition == itemCount - 2) {
                    layoutPosition - 1
                } else {
                    layoutPosition
                }
                animateRemoval {
                    removeItem(layoutPosition)
                    notifyItemRemoved(layoutPosition)
                    notifyItemChanged(currentIndex)
                }
            }
        }
    }

    enum class ViewType(val value: Int) {
        ADD_TAB(0),
        NORMAL_TAB(1);
    }

    data class TabViewModel(
        val bowlerId: Long = 0,
        val bowlerName: String = ""
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
