package com.eudycontreras.bowlingcalculator.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.DialogFragment
import com.eudycontreras.bowlingcalculator.R
import com.eudycontreras.bowlingcalculator.components.views.ViewComponent
import com.eudycontreras.bowlingcalculator.utilities.extensions.addTouchAnimation
import com.eudycontreras.bowlingcalculator.utilities.extensions.dimensions

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

abstract class DialogFragment: DialogFragment(), ViewComponent {

    companion object {
        val STYLE_NORMAL = 0
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

    override fun assignInteraction(view: View?) {
        view?.addTouchAnimation(
            clickTarget = null,
            scale = 0.95f,
            interpolatorPress =  DecelerateInterpolator(),
            interpolatorRelease = OvershootInterpolator()
        )
    }
}