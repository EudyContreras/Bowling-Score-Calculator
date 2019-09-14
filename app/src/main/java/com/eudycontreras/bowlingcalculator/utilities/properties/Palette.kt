package com.eudycontreras.bowlingcalculator.utilities.properties

class Palette(
    colorPrimary: Int,
    colorPrimaryDark: Int,
    colorPrimaryLight: Int
) {
    var colorPrimary: Int = colorPrimary
        private set
    var colorPrimaryDark: Int = colorPrimaryDark
        private set
    var colorPrimaryLight: Int = colorPrimaryLight
        private set

    companion object {
        fun of(color: Int): Palette{
            val colorObj = MutableColor.fromColor(color)
            return Palette(
                color,
                colorObj.darker(21).toColor(),
                colorObj.brigher(16).toColor()
            )
        }
    }
}