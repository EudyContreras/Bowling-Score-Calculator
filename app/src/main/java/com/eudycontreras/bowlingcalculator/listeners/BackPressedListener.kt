package com.eudycontreras.bowlingcalculator.listeners


interface BackPressedListener {
    fun onBackPressed()
    fun disallowExit(): Boolean
}