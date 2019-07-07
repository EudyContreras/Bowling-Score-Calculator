package com.eudycontreras.bowlingcalculator.libraries.morpher.utilities

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

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
object ColorUtility {

    fun colorDecToHex(r: Int, g: Int, b: Int): Int {
        return Color.parseColor(colorDecToHexString(r, g, b))
    }

    fun colorDecToHex(a: Int, r: Int, g: Int, b: Int): Int {
        return Color.parseColor(colorDecToHexString(a, r, g, b))
    }

    fun colorDecToHexString(r: Int, g: Int, b: Int): String {
        return colorDecToHexString(255, r, g, b)
    }

    fun colorDecToHexString(a: Int, r: Int, g: Int, b: Int): String {
        var red = Integer.toHexString(r)
        var green = Integer.toHexString(g)
        var blue = Integer.toHexString(b)
        var alpha = Integer.toHexString(a)

        if (red.length == 1) {
            red = "0$red"
        }
        if (green.length == 1) {
            green = "0$green"
        }
        if (blue.length == 1) {
            blue = "0$blue"
        }
        if (alpha.length == 1) {
            alpha = "0$alpha"
        }

        return "#$alpha$red$green$blue"
    }

    fun interpolateColor(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff

        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff

        return startA + (fraction * (endA - startA)).toInt() shl 24 or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                startB + (fraction * (endB - startB)).toInt()
    }

    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun adjustAlpha(color: MorphColor, factor: Float) {
        color.alpha = (color.alpha * factor).roundToInt()
    }

    fun toSoulColor(@ColorInt color: Int): MorphColor {
        val alpha = Color.alpha(color)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return MorphColor(alpha, red, green, blue)
    }

    data class MorphColor(
        var red: Int = 0,
        var green: Int = 0,
        var blue: Int = 0,
        var alpha: Int = 0
    ) {
        constructor(red: Int, green: Int, blue: Int) : this(1, red, green, blue)

        constructor(color: Int = 0x000000): this(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color))

        fun setColor(color: Int) {
            this.alpha = Color.alpha(color)
            this.red = Color.red(color)
            this.green = Color.green(color)
            this.blue = Color.blue(color)
        }

        fun setAlpha(alpha: Float) {
            this.alpha = (255f * alpha).roundToInt()
        }

        fun toColor(): Int {
            return Color.argb(alpha, red, green, blue)
        }

        companion object {

            fun copy(color: MorphColor): MorphColor {
                return MorphColor(color.alpha, color.red, color.green, color.blue)
            }
        }
    }
}
