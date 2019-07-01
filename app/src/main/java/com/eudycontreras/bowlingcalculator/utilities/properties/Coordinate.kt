package com.eudycontreras.bowlingcalculator.utilities.properties

/**
 * Created by eudycontreras.
 */

data class Coordinate(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun copyProps(): Coordinate {
        return Coordinate(x, y)
    }
}