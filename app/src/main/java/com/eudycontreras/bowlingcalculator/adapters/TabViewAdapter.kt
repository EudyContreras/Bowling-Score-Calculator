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
 * @Project BowlingCalculator
 * @author Eudy Contreras.
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
            const val alpha = 0.4f
            const val changeDuration = 400L
            const val removeDuration = 200L
            val translateY = 4.dp
            val translateZ = (-10).dp
        }
    }

    fun addItem(item: TabViewModel, selectedIndex: Int? = null, manual: Boolean = false) {
        lastTab?.let { reference ->
            if (!reference.isEnqueued) {
                (reference.get() as TabViewAdapter.TabViewHolderNormal?)?.deactivateTab()
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
                (reference.get() as TabViewAdapter.TabViewHolderNormal?)?.deactivateTab()
            }
        }

        val isManual = manual && this.items.size > 1

        items.asReversed().forEach {
            this.items.add(currentIndex, it)
            notifyItemInserted(currentIndex)
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
                viewComponent.controller.requestTab(true)
            }
        }
    }

    inner class TabViewHolderNormal(view: View) : TabViewHolder(view){

        private val tabItem: ConstraintLayout = view.tabItem
        private val tabLabel: TextView = tabItem.tabItemLabel

        private val tabAction: FrameLayout = view.tabItemAction

        private var model: TabViewModel? = null

        private var removed = false

        init {
            resetValues()
            registerListeners()

            this.tabItem.addTouchAnimation(
                clickTarget = null,
                depth = (-8).dp,
                interpolatorPress = DecelerateInterpolator(),
                interpolatorRelease = OvershootInterpolator()
            )
        }

        override fun registerListeners(){
            tabItem.setOnClickListener(this)

            tabAction.setOnClickListener {
                if (!removed) {
                    removeTab()
                }
            }
        }

        override fun onClick(view: View?) {
            if (currentIndex == layoutPosition) {
                return
            }
            currentIndex = layoutPosition
            viewComponent.controller.onTabSelection(layoutPosition, true)
            lastTab?.let { reference ->
                if (!reference.isEnqueued) {
                    (reference.get() as TabViewHolderNormal?)?.deactivateTab()
                }
            }
            activateTab()
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
                .alpha(1f)
                .setListener(null)
                .translationY(0f)
                .translationZ(0.dp)
                .setDuration(Values.changeDuration)
                .start()
        }

        fun deactivateTab() {
            this.tabAction.detach()
            this.tabItem.animate()
                .alpha(Values.alpha)
                .setListener(null)
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
            val lastTabIndex = currentIndex
            removed = true
            currentIndex = if (layoutPosition == (itemCount - 2) && layoutPosition != 0) {
                layoutPosition - 1
            } else {
                layoutPosition
            }
            viewComponent.controller.removeTab(lastTabIndex, currentIndex) {
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
