package com.eudycontreras.bowlingcalculatorchallenge.view_components

import android.view.View

/**
 * Created by eudycontreras.
 */
interface ViewComponent {
    fun setDefaultValues()
    fun registerListeners()
    fun assignInteraction(view: View?)
}