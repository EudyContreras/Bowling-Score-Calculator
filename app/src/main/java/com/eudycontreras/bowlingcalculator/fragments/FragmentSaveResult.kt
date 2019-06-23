package com.eudycontreras.bowlingcalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import kotlinx.android.synthetic.main.dialog_save_result.*
import kotlinx.android.synthetic.main.dialog_save_result.view.*

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class FragmentSaveResult: DialogFragment() {

    var controller: ActionViewController? = null

    private lateinit var saveDialog: View

    companion object {
        fun instance(controller: ActionViewController): FragmentSaveResult {
            val fragment = FragmentSaveResult()
            fragment.controller = controller
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        saveDialog = inflater.inflate(R.layout.dialog_save_result, container) as ConstraintLayout
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        setDefaultValues()
        registerListeners()

        return saveDialog
    }

    override fun setDefaultValues() {
        assignInteraction(saveDialog.dialogSaveCancel)
        assignInteraction(saveDialog.dialogSaveSubmit)
    }

    override fun registerListeners() {
        saveDialog.dialogSaveCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        saveDialog.dialogSaveSubmit.setOnClickListener {
            val name = saveDialogInput.text.toString()
            if(!name.isBlank()) {
                controller?.saveCurrentResult(name) {
                    activity?.onBackPressed()
                }
            } else {
                Toast.makeText(context, context?.getString(R.string.no_result_name), Toast.LENGTH_LONG).show()
            }
        }
    }
}