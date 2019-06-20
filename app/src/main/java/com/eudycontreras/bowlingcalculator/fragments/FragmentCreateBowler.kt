package com.eudycontreras.bowlingcalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eudycontreras.bowlingcalculator.BowlerListener
import com.eudycontreras.bowlingcalculator.MAX_ALLOWED_INPUT_FIELDS
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.adapters.InputViewAdapter
import com.eudycontreras.bowlingcalculator.components.controllers.TabsViewController
import kotlinx.android.synthetic.main.dialog_create_bowlers.view.*

/**
 * Created by eudycontreras.
 */

class FragmentCreateBowler: DialogFragment() {

    private var controller: TabsViewController? = null

    private var adapter: InputViewAdapter? = null

    private var listener: BowlerListener = null

    private lateinit var createDialog: ConstraintLayout

    companion object {
        fun instance(controller: TabsViewController, listener: BowlerListener = null): FragmentCreateBowler {
            val fragment = FragmentCreateBowler()
            fragment.listener = listener
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
                    listener?.invoke(it)
                    dismiss()
                }
            } else {
                Toast.makeText(context, context?.getString(R.string.no_result_name), Toast.LENGTH_LONG).show()
            }
        }
    }
}