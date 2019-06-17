package com.eudycontreras.bowlingcalculator.components.views

import android.view.View

/**
 * Created by eudycontreras.
 */
interface ViewComponent {
    fun setDefaultValues()
    fun registerListeners()
    fun assignInteraction(view: View?)
}