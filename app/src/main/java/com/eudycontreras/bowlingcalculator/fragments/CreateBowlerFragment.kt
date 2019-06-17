package com.eudycontreras.bowlingcalculator.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.MAX_ALLOWED_INPUT_FIELDS
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.adapters.InputViewAdapter
import com.eudycontreras.bowlingcalculator.components.controllers.TabsViewController
import com.eudycontreras.bowlingcalculator.components.views.ViewComponent
import com.eudycontreras.bowlingcalculator.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.extensions.dimensions
import kotlinx.android.synthetic.main.dialog_create_bowlers.view.*

/**
 * Created by eudycontreras.
 */

class CreateBowlerFragment: DialogFragment(), ViewComponent {

    private var controller: TabsViewController? = null

    private var adapter: InputViewAdapter? = null

    private lateinit var createDialog: ConstraintLayout

    companion object {
        fun instance(controller: TabsViewController): CreateBowlerFragment {
            val fragment = CreateBowlerFragment()
            fragment.controller = controller
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        createDialog = inflater.inflate(R.layout.dialog_create_bowlers, container) as ConstraintLayout
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)

        setDefaultValues()
        registerListeners()

        return createDialog
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.let {
            val layoutParams = it.attributes
            layoutParams.width = activity!!.dimensions().first - (resources.getDimension(R.dimen.margin_dialog).toInt() * 2)
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
            it.setGravity(Gravity.CENTER)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return dialog
    }

    override fun setDefaultValues() {
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

    override fun registerListeners() {
        createDialog.createDialogAddInput.setOnClickListener {
            adapter?.let {
                if(it.itemCount < MAX_ALLOWED_INPUT_FIELDS) {
                    it.addItem()
                } else {
                    Toast.makeText(context, R.string.max_input_count_reached, Toast.LENGTH_SHORT).show()
                }
            }
        }

        createDialog.dialogCreateCancel.setOnClickListener {
            dismiss()
        }

        createDialog.dialogCreateSubmit.setOnClickListener {
            val names = adapter!!.getItems()

            if(!names.isEmpty()) {
                controller?.createBowler(names) {
                    dismiss()
                }
            } else {
                Toast.makeText(context, context?.getString(R.string.no_result_name), Toast.LENGTH_LONG).show()
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
}