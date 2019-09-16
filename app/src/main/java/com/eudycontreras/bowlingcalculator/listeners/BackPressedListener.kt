package com.eudycontreras.bowlingcalculator.listeners


/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

interface BackPressedListener {
    fun onBackPressed()
    fun disallowExit(): Boolean
}