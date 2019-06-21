package com.eudycontreras.bowlingcalculator.calculator.listeners

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler

/**
 * Created by eudycontreras.
 */

interface ScoreStateListener {
    fun onScoreUpdated(bowler: Bowler, totalScore: Int, totalPossible: Int)
}