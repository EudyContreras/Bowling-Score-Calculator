/*
package com.eudycontreras.bowlingcalculator.fragments

import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.adapters.InputViewAdapter
import com.eudycontreras.bowlingcalculator.components.controllers.TabsViewController
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts.ConstraintLayout
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_create_bowlers.view.*

*/
/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 *//*


class FragmentCreateBowler(val context: MainActivity) {

    private var controller: TabsViewController? = null

    private var adapter: InputViewAdapter? = null

    private var bowlerListener: BowlerListener = null
    private var dismissListener: (() -> Unit)? = null

    private var manualRequest: Boolean = false

    private var fromEmptyState: Boolean = false

    private lateinit var createDialog: ConstraintLayout

    companion object {
        fun instance(controller: TabsViewController, manual: Boolean, listener: BowlerListener = null, onDismiss: (() -> Unit)? = null): FragmentCreateBowler {
            val fragment = FragmentCreateBowler(controller.context)
            fragment.bowlerListener = listener
            fragment.dismissListener = onDismiss
            fragment.controller = controller
            fragment.manualRequest = manual
            fragment.initialize()
            return fragment
        }
    }

    private fun initialize() {
        createDialog = context.dialog as ConstraintLayout
        setDefaultValues()
        registerListeners()
    }

    fun getContainerView(): ConstraintLayout {
        return createDialog
    }

    fun assignInteraction(view: View?) {
        view?.addTouchAnimation(
            clickTarget = null,
            scale = 0.95f,
            interpolatorPress =  DecelerateInterpolator(),
            interpolatorRelease = OvershootInterpolator()
        )
    }

    private fun setDefaultValues() {
        assignInteraction(createDialog.dialogCreateCancel)
        assignInteraction(createDialog.dialogCreateSubmit)
        assignInteraction(createDialog.createDialogAddInput)

        createDialog.createDialogInputRecycler?.let {
            adapter = InputViewAdapter(context, this)
            it.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            it.recycledViewPool.setMaxRecycledViews(0, 0)
            it.setItemViewCacheSize(10)
            it.adapter = adapter
        }
    }

    private fun registerListeners() {
    }

    fun show(duration: Long = Morpher.DEFAULT_DURATION, fromEmptyState: Boolean = false) {
        this.fromEmptyState = fromEmptyState
        createDialog.createDialogInputRecycler?.let {
            adapter = InputViewAdapter(context, this)
            it.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            it.recycledViewPool.setMaxRecycledViews(0, 0)
            it.setItemViewCacheSize(10)
            it.adapter = adapter
        }

        context.morphTransitioner.endView = createDialog

        context.morphTransitioner.morphInto(duration, onEnd = {
            createDialog.createDialogInputRecycler?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        })
    }
}*/
