package com.eudycontreras.bowlingcalculator.calculator.elements

import java.io.Serializable

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

interface Element : Serializable {
    fun init()
    fun reset()
}
