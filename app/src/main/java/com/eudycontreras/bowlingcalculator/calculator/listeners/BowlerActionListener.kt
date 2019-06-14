package com.eudycontreras.bowlingcalculator.calculator.listeners

/**
 * Created by eudycontreras.
 */

interface BowlerActionListener {

    fun throwBall(pinKnockedCount: Int)
    fun onFrameSelected(frameIndex: Int)
    fun clearScore()
}