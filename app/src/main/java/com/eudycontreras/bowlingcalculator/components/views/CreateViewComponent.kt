package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.adapters.InputViewAdapter
import com.eudycontreras.bowlingcalculator.components.controllers.TabsViewController
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts.ConstraintLayout
import com.eudycontreras.bowlingcalculator.listeners.PaletteListener
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.MAX_ALLOWED_INPUT_FIELDS
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.extensions.toStateList
import com.eudycontreras.bowlingcalculator.utilities.properties.Palette
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_create_bowlers.view.*

class CreateViewComponent(
    private val context: MainActivity,
    val controller: TabsViewController
) : ViewComponent, PaletteListener {

    val parentView: ConstraintLayout = context.dialog as ConstraintLayout

    private var adapter: InputViewAdapter? = null

    private var bowlerListener: BowlerListener = null
    private var dismissListener: (() -> Unit)? = null

    private var manualRequest: Boolean = false

    private var fromEmptyState: Boolean = false

    fun setOptions(manual: Boolean, listener: BowlerListener = null, onDismiss: (() -> Unit)? = null) {
        bowlerListener = listener
        dismissListener = onDismiss
        manualRequest = manual

        setDefaultValues()
        registerListeners()
    }

    override fun setDefaultValues() {
        assignInteraction(parentView.dialogCreateCancel)
        assignInteraction(parentView.dialogCreateSubmit)
        assignInteraction(parentView.createDialogAddInput)

        parentView.createDialogInputRecycler?.let {
            adapter = InputViewAdapter(context, this)
            it.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            it.recycledViewPool.setMaxRecycledViews(0, 0)
            it.setItemViewCacheSize(10)
            it.adapter = adapter
        }
    }

    override fun registerListeners() {
        parentView.createDialogAddInput.setOnClickListener {
            adapter?.let {
                if(it.itemCount < MAX_ALLOWED_INPUT_FIELDS) {
                    it.addItem()
                } else {
                    Toast.makeText(context, R.string.max_input_count_reached, Toast.LENGTH_SHORT).show()
                }
            }
        }

        parentView.dialogCreateCancel.setOnClickListener {
            dismiss(Morpher.DEFAULT_DURATION)
        }

        parentView.dialogCreateSubmit.setOnClickListener {
            val names = adapter!!.getItems().asReversed().map { it.trim() }

            if(names.isNotEmpty()) {
                context.hideOverlay()
                context.morphTransitioner.morphFrom(Morpher.DEFAULT_DURATION, onEnd = {
                    controller.createBowler(names, manualRequest) {
                        bowlerListener?.invoke(it)
                        dismissListener?.invoke()
                    }
                })
            } else {
                Toast.makeText(context, context.getString(R.string.no_result_name), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun assignInteraction(view: View?) {
        view?.addTouchAnimation(
            clickTarget = null,
            scale = 0.95f,
            interpolatorPress =  DecelerateInterpolator(),
            interpolatorRelease = OvershootInterpolator()
        )
    }

    fun show(duration: Long = Morpher.DEFAULT_DURATION, fromEmptyState: Boolean = false) {
        this.fromEmptyState = fromEmptyState
        parentView.createDialogInputRecycler?.let {
            adapter = InputViewAdapter(context, this)
            it.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            it.recycledViewPool.setMaxRecycledViews(0, 0)
            it.setItemViewCacheSize(10)
            it.adapter = adapter
        }

        context.morphTransitioner.endView = parentView

        context.morphTransitioner.morphInto(duration, onEnd = {
            parentView.createDialogInputRecycler?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        })
    }

    private fun dismiss(duration: Long) {
        context.hideOverlay()
        dismissListener?.invoke()
        context.morphTransitioner.morphFrom(duration)
    }

    override fun onNewPalette(palette: Palette) {
        parentView.createDialogAddInput.backgroundTintList = palette.colorPrimary.toStateList()
    }
}