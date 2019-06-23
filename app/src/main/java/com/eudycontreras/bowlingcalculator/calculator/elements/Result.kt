package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.utilities.NO_ID
import java.util.*
import kotlin.collections.ArrayList


/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

data class Result (
    val name: String,
    val date: Date
) {
    var id: Long = NO_ID
    var bowlers: List<Bowler> = ArrayList()
}