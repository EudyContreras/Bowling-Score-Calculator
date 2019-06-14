package com.eudycontreras.bowlingcalculator.extensions

import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll

/**
 * Created by eudycontreras.
 */

fun List<Roll>.sum() = map { it.totalKnockdown }.sum()

fun List<Frame>.getComputedScore(): Int {
    val scoreAccumulator = this
        .map { it.rolls.values }
        .flatten()
        .sumBy { it.totalKnockdown }

    val bonusAccumulator = this.map { it.bonusPoints }.sum()

    return scoreAccumulator + bonusAccumulator
}
