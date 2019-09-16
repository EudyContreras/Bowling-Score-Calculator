package com.eudycontreras.bowlingcalculator.components.views

import android.view.View
import androidx.lifecycle.ViewModel


/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

abstract class ViewComponent: ViewModel(){
    abstract fun setDefaultValues()
    abstract fun registerListeners()
    abstract fun assignInteraction(view: View?)
}