package com.eudycontreras.bowlingcalculator.libraries.morpher.extensions

import android.content.res.Resources

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