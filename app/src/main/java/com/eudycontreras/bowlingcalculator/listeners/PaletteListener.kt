package com.eudycontreras.bowlingcalculator.listeners

import com.eudycontreras.bowlingcalculator.utilities.properties.Palette

interface PaletteListener {
    fun onNewPalette(palette: Palette)
}