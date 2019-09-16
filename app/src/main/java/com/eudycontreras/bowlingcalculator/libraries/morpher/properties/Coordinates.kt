package com.eudycontreras.bowlingcalculator.libraries.morpher.properties

/**
 * <h1>Class description!</h1>
 *
 *
 *
 * **Note:** Unlicensed private property of the author and creator
 * unauthorized use of this class outside of the Soul Vibe project
 * may result on legal prosecution.
 *
 *
 * Created by <B>Eudy Contreras</B>
 *
 * @author  Eudy Contreras
 * @version 1.0
 * @since   2018-03-31
 */
data class Coordintates(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun copy(): Coordintates {
        return Coordintates(x, y)
    }

    fun midPoint(other: Coordintates): Coordintates {
        return Companion.midPoint(this, other)
    }

    companion object {
        fun midPoint(start: Coordintates, end: Coordintates): Coordintates {
            return Coordintates((start.x + end.x) / 2 , (start.y + end.y) / 2)
        }
    }
}