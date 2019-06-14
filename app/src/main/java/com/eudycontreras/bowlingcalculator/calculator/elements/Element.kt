package com.eudycontreras.bowlingcalculator.calculator.elements

import java.io.Serializable

/**
 * Created by eudycontreras.
 */
interface Element : Serializable {
    fun init()
    fun reset()
}
