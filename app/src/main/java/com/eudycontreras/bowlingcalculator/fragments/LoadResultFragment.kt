package com.eudycontreras.bowlingcalculator.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.components.controllers.ActionViewController
import com.eudycontreras.bowlingcalculator.components.views.ViewComponent
import com.eudycontreras.bowlingcalculator.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.extensions.dimensions
import kotlinx.android.synthetic.main.dialog_load_result.view.*


/**
 * Created by eudycontreras.
 */

class LoadResultFragment: DialogFragment(), ViewComponent {

    var controller: ActionViewController? = null

    private lateinit var loadDialog: ConstraintLayout

    companion object {
        fun instance(controller: ActionViewController): LoadResultFragment {
            val fragment = LoadResultFragment()
            fragment.controller = controller
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loadDialog = inflater.inflate(R.layout.dialog_load_result, container) as ConstraintLayout
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        setDefaultValues()
        registerListeners()

        return loadDialog
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
        assignInteraction(loadDialog.dialogLoadClear)
    }

    override fun registerListeners() {
        loadDialog.dialogLoadClear.setOnClickListener {

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