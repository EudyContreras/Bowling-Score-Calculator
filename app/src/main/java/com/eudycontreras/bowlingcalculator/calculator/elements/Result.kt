package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.utilities.NO_ID
import java.util.*
import kotlin.collections.ArrayList

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

data class Result (
    val name: String,
    val date: Date
) {
    var id: Long = NO_ID
    var bowlers: List<Bowler> = ArrayList()
}