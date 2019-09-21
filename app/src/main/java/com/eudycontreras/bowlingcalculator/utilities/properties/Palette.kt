package com.eudycontreras.bowlingcalculator.utilities.properties

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
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