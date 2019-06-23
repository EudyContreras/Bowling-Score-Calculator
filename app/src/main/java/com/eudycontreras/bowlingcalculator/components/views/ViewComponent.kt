package com.eudycontreras.bowlingcalculator.components.views

import android.view.View

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface ViewComponent {
    fun setDefaultValues()
    fun registerListeners()
    fun assignInteraction(view: View?)
}