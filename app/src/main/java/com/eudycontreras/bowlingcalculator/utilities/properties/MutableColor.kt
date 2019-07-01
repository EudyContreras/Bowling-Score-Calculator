package com.eudycontreras.bowlingcalculator.utilities.properties

import androidx.annotation.ColorInt
import com.eudycontreras.bowlingcalculator.utilities.AndroidColor
import kotlin.math.roundToInt


/**
 * Created by eudycontreras.
 */

data class MutableColor(
    private var alpha: Int = 255,
    private var red: Int = 0,
    private var green: Int = 0,
    private var blue: Int = 0
) : Color() {

    constructor(@ColorInt color: Int) : this() {
        this.alpha = AndroidColor.alpha(color)
        this.red = AndroidColor.red(color)
        this.green = AndroidColor.green(color)
        this.blue = AndroidColor.blue(color)
    }

    constructor(color: MutableColor) : this() {
        this.alpha = color.alpha
        this.red = color.red
        this.green = color.green
        this.blue = color.blue
    }

    fun updateColor(
        alpha: Int = 0,
        red: Int = 0,
        green: Int = 0,
        blue: Int = 0
    ): MutableColor {
        this.alpha = alpha
        this.red = red
        this.green = green
        this.blue = blue
        this.colorChanged = true
        return this
    }

    fun updateAlpha(alpha: Int): MutableColor {
        if (alpha != this.alpha) {
            this.colorChanged = true
        }
        this.alpha = alpha
        return this
    }

    fun updateAlpha(alpha: Float): MutableColor {
        if ((alpha * 255f).toInt() != this.alpha) {
            this.colorChanged = true
        }
        this.alpha = (alpha * 255f).toInt()
        return this
    }

    override fun getOpacity(): Float {
        return (alpha / 255f)
    }

    fun subtractAlpha(amount: Float): MutableColor {
        val color = MutableColor(this)
        color.alpha -= (Math.round(amount * 255))
        return color
    }

    fun adjust(amount: Float): MutableColor {
        val color = MutableColor(this)
        color.red = (this.red * amount).toInt()
        color.green = (this.green * amount).toInt()
        color.blue = (this.blue * amount).toInt()

        color.red = clamp(color.red)
        color.green = clamp(color.green)
        color.blue = clamp(color.blue)
        return color
    }

    fun addAlpha(amount: Float): MutableColor {
        alpha += clamp((Math.round(amount * 255)))
        this.colorChanged = true
        return this
    }

    fun subtractAlpha(amount: Int): MutableColor {
        alpha = clamp(alpha - amount)
        this.colorChanged = true
        return this
    }

    fun addAlpha(amount: Int): MutableColor {
        alpha = clamp(alpha + amount)
        this.colorChanged = true
        return this
    }

    fun addRed(amount: Int): MutableColor {
        red = clamp(red + amount)
        this.colorChanged = true
        return this
    }

    fun addGreen(amount: Int): MutableColor {
        green = clamp(green + amount)
        this.colorChanged = true
        return this
    }

    fun addBlue(amount: Int): MutableColor {
        blue = clamp(blue + amount)
        this.colorChanged = true
        return this
    }

    fun subtractRed(amount: Int): MutableColor {
        red = clamp(red - amount)
        this.colorChanged = true
        return this
    }

    fun subtractGreen(amount: Int): MutableColor {
        green = clamp(green - amount)
        this.colorChanged = true
        return this
    }

    fun subtractBlue(amount: Int): MutableColor {
        blue = clamp(blue - amount)
        this.colorChanged = true
        return this
    }

    override fun reset(){
        this.alpha = 255
        this.red = 255
        this.green = 255
        this.blue = 255
    }

    override fun getAlpha() = alpha

    override fun getRed() = red

    override fun getGreen() = green

    override fun getBlue() = blue

    fun setColor(color: MutableColor): MutableColor {
        this.alpha = color.alpha
        this.red = color.red
        this.green = color.green
        this.blue = color.blue

        this.colorChanged = true
        return this
    }

    fun setColor(red: Int, green: Int, blue: Int, alpha: Int): MutableColor {
        this.alpha = alpha
        this.red = red
        this.green = green
        this.blue = blue

        this.colorChanged = true
        return this
    }

    override fun toColor(): Int {
        return if (mTempColor == -1) {
            mTempColor = AndroidColor.argb(alpha, red, green, blue)
            mTempColor
        } else {
            if (colorChanged) {
                mTempColor = AndroidColor.argb(alpha, red, green, blue)
                colorChanged = false
                mTempColor
            } else {
                mTempColor
            }
        }
    }

    fun copy(): MutableColor {
        return MutableColor(alpha, red, green, blue)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MutableColor) return false

        if (alpha != other.alpha) return false
        if (red != other.red) return false
        if (green != other.green) return false
        if (blue != other.blue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alpha
        result = 31 * result + red
        result = 31 * result + green
        result = 31 * result + blue
        return result
    }

    companion object {

        val WHITE: Color = MutableColor(255, 255, 255, 255)
        val BLACK: Color = MutableColor(255, 0, 0, 0)
        val RED: Color = MutableColor(255, 255, 0, 0)
        val GREEN: Color = MutableColor(255, 0, 255, 0)
        val BLUE: Color = MutableColor(255, 0, 0, 255)
        val YELLOW: Color = MutableColor(255, 255, 255, 0)
        val PURPLE: Color = MutableColor(255, 255, 0, 255)

        val DEFAULT: Color = MutableColor()

        fun adjustAlpha(color: MutableColor, factor: Float) {
            color.updateAlpha(((color.alpha * factor).roundToInt()))
        }

        fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
            val alpha = (AndroidColor.alpha(color) * factor).roundToInt()
            val red = AndroidColor.red(color)
            val green = AndroidColor.green(color)
            val blue = AndroidColor.blue(color)

            return AndroidColor.argb(alpha, red, green, blue)
        }

        fun interpolateColor(start: MutableColor, end: MutableColor, amount: Float, result: MutableColor) {
            result.setColor(start)
            result.updateColor(
                red = ((start.red + (end.red - start.red) * amount).toInt()),
                green = ((start.green + (end.green - start.green) * amount).toInt()),
                blue = ((start.blue + (end.blue - start.blue) * amount).toInt())
            )
        }

        fun rgb(red: Int, green: Int, blue: Int): MutableColor {
            return MutableColor(
                alpha = 255,
                red = red,
                green = green,
                blue = blue
            )
        }

        fun rgb(rgb: Int): MutableColor {
            return MutableColor(
                alpha = 255,
                red = rgb,
                green = rgb,
                blue = rgb
            )
        }

        fun rgba(red: Int, green: Int, blue: Int, alpha: Int): MutableColor {
            return MutableColor(
                alpha = alpha,
                red = red,
                green = green,
                blue = blue
            )
        }

        fun rgba(rgb: Int, alpha: Float): MutableColor {
            return MutableColor(
                alpha = (alpha * 255).toInt(),
                red = rgb,
                green = rgb,
                blue = rgb
            )
        }

        fun rgba(red: Int, green: Int, blue: Int, alpha: Float): MutableColor {
            return MutableColor(
                alpha = (alpha * 255).toInt(),
                red = red,
                green = green,
                blue = blue
            )
        }

        fun toColor(@ColorInt color: Int): MutableColor {
            return MutableColor(color)
        }

        fun fromColor(color: Int): MutableColor {
            return MutableColor(color)
        }

        fun fromColor(color: Color): MutableColor {
            return MutableColor(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue())
        }

        fun fromHexString(color: String): MutableColor {
            return MutableColor(AndroidColor.parseColor(color))
        }

        fun random(): MutableColor {
            return MutableColor(
                255,
                (50 until 205).random(),
                (50 until 205).random(),
                (50 until 205).random()
            )
        }

        fun randomBlue(): MutableColor {
            return MutableColor(
                255,
                20,
                (100 until 205).random(),
                (130 until 245).random()
            )
        }
    }
}