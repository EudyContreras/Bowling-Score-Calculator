package com.eudycontreras.bowlingcalculator.calculator.listeners

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface BowlerActionListener {
    fun throwBall(pinKnockedCount: Int)
    fun onFrameSelected(frameIndex: Int)
    fun clearScore()
}