package com.eudycontreras.bowlingcalculator.listeners

import com.eudycontreras.bowlingcalculator.utilities.properties.Palette

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

interface PaletteListener {
    fun onNewPalette(palette: Palette)
}