package com.eudycontreras.bowlingcalculator.utilities.extensions

import android.content.res.ColorStateList
import android.content.res.Resources

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Float.dp: Float
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

fun Int.toStateList(): ColorStateList {
    return ColorStateList.valueOf(this)
}