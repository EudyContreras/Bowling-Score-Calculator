package com.eudycontreras.bowlingcalculator.calculator.listeners

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

interface ScoreStateListener {
    fun onScoreUpdated(bowler: Bowler, totalScore: Int, totalPossible: Int)
}