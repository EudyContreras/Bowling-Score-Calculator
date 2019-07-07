package com.eudycontreras.bowlingcalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.activities.MainActivity
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.components.controllers.EmptyStateViewController
import com.eudycontreras.bowlingcalculator.components.views.EmptyStateViewComponent
import kotlinx.android.synthetic.main.dialog_load_result.view.*


/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class FragmentLoadResult: DialogFragment() {

    var controller: ActionViewController? = null

    private lateinit var loadDialog: ConstraintLayout

    private lateinit var emptyState: EmptyStateViewController

    companion object {
        fun instance(controller: ActionViewController): FragmentLoadResult {
            val fragment = FragmentLoadResult()
            fragment.controller = controller
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loadDialog = inflater.inflate(R.layout.dialog_load_result, container) as ConstraintLayout
        emptyState = EmptyStateViewController(activity as MainActivity, loadDialog.emptyStateResults)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        setDefaultValues()
        registerListeners()

        return loadDialog
    }

    override fun setDefaultValues() {
        emptyState.setState(EmptyStateViewComponent.EmptyState.Results(activity as MainActivity))
        assignInteraction(loadDialog.dialogLoadClear)
    }

    override fun registerListeners() {
        loadDialog.dialogLoadClear.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()
        emptyState.revealState {

        }
    }
}