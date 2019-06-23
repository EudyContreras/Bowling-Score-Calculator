package com.eudycontreras.bowlingcalculator.calculator.listeners

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface ScoreStateListener {
    fun onScoreUpdated(bowler: Bowler, totalScore: Int, totalPossible: Int)
}