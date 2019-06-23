package com.eudycontreras.bowlingcalculator.extensions

import android.content.res.Resources

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

fun Int.next(shift: Int = 1 ): Int {
    return this + shift
}

fun Int.previous(shift: Int = 1): Int {
    return this - shift
}

fun Int.clamp(min: Int, max: Int): Int {
    return if (this < min) min else if (this > max) max else this
}
