package com.eudycontreras.bowlingcalculator.calculator.listeners

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame

/**
 * Created by eudycontreras.
 */

interface ScoreStateListener {
    fun onScoreUpdated(bowler: Bowler, current: Frame, frames: List<Frame>, totalScore: Int, totalPossible: Int)
}