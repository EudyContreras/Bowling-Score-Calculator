package com.eudycontreras.bowlingcalculator.extensions

import android.content.res.Resources

/**
 * Created by eudycontreras.
 */

val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

fun Int.clamp(min: Int, max: Int): Int {
    return if (this < min) min else if (this > max) max else this
}
