package com.eudycontreras.bowlingcalculator.utilities.properties

import com.eudycontreras.bowlingcalculator.utilities.AndroidColor


/**
 * Created by eudycontreras.
 */

abstract class Color {

    protected var mTempColor: Int = -1

    protected var colorChanged: Boolean = false

    abstract fun getOpacity(): Float

    abstract fun getAlpha(): Int

    abstract fun getRed(): Int

    abstract fun getGreen(): Int

    abstract fun getBlue(): Int

    abstract fun toColor(): Int

    abstract fun reset()

    protected fun clamp(color: Int): Int {
        return when {
            color > 255 -> 255
            color < 0 -> 0
            else -> color
        }
    }

    companion object {

        fun colorDecToHex(r: Int, g: Int, b: Int): Int {
            return AndroidColor.parseColor(colorDecToHexString(r, g, b))
        }

        fun colorDecToHex(a: Int, r: Int, g: Int, b: Int): Int {
            return AndroidColor.parseColor(colorDecToHexString(a, r, g, b))
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
    }
}