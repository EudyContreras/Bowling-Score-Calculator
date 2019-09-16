package com.eudycontreras.bowlingcalculator.libraries.morpher.properties

/**
 * Copyright (C) 2019 Motion Morpher Project
 *
 **Note:** Unlicensed private property of the author and creator
 * unauthorized use of this class outside of the Motion Morpher project
 * or other projects to which the author has explicitly added this library
 * may result on legal prosecution.
 *
 * @Project Motion Morpher
 * @author Eudy Contreras.
 * @since March 2019
 */
data class Coordinates(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun copy(): Coordinates {
        return Coordinates(x, y)
    }

    fun midPoint(other: Coordinates): Coordinates {
        return Companion.midPoint(this, other)
    }

    companion object {
        fun midPoint(start: Coordinates, end: Coordinates): Coordinates {
            return Coordinates((start.x + end.x) / 2 , (start.y + end.y) / 2)
        }
    }
}