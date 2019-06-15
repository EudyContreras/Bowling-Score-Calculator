package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.NO_ID
import java.util.*


/**
 * Created by eudycontreras.
 */

data class Result (
    val name: String,
    val date: Date
) {
    var id: Long = NO_ID
    var bowlers: List<Bowler> = ArrayList()
}