package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_PIN_COUNT


/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * A bowling roll made by the bowler. The result of
 * the roll is partially determined by the bowler's skill
 *
 * @property result the result that the roll yielded
 * @constructor Creates a bowling roll
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras
 * @since January 2019
 */

data class Roll(
    var bowlerId: Long,
    var totalKnockdown: Int = 0,
    var result: Result = Result.UNKNOWN
): Element {
    var frameIndex: Int = 0

    var parentState: Frame.State = Frame.State.FIRST_CHANCE

    override fun init() { }

    override fun reset() {
        frameIndex = 0
        totalKnockdown = 0
        result = Result.UNKNOWN
        parentState = Frame.State.FIRST_CHANCE
    }

    override fun toString(): String {
        return "Roll(totalKnockdown=$totalKnockdown, result=$result, parentState=$parentState)"
    }

    enum class Result {
        STRIKE,
        SPARE,
        MISS,
        NORMAL,
        UNKNOWN;

        override fun toString(): String{
            return name
        }

        companion object {
            fun from(lastRoll: Roll?, pinKnockedCount: Int): Result {
               var result = when(pinKnockedCount) {
                   0 -> MISS
                   in 1..9 -> NORMAL
                   DEFAULT_PIN_COUNT -> STRIKE
                   else -> UNKNOWN
                }

                lastRoll?.let {
                    if ((pinKnockedCount + it.totalKnockdown) == DEFAULT_PIN_COUNT) {
                        if (result != MISS && it.result != STRIKE) {
                            result = SPARE
                        } else {

                        }
                    }
                }
                return result
            }
        }
    }
}