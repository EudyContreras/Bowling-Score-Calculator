package com.eudycontreras.bowlingcalculator.utilities.properties

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
data class Coordinate(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun copyProps(): Coordinate {
        return Coordinate(x, y)
    }
}