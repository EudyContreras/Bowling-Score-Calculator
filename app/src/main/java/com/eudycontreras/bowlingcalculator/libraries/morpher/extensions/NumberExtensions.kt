package com.eudycontreras.bowlingcalculator.libraries.morpher.extensions

import android.content.res.Resources

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
val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

fun Int.clamp(min: Int, max: Int): Int {
    return if (this < min) min else if (this > max) max else this
}

fun Float.clamp(min: Float, max: Float): Float {
    return if (this < min) min else if (this > max) max else this
}