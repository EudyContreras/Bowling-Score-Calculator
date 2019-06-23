package com.eudycontreras.bowlingcalculator.calculator.elements

import java.io.Serializable

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface Element : Serializable {
    fun init()
    fun reset()
}
