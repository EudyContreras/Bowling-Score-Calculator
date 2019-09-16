package com.eudycontreras.bowlingcalculator.calculator.listeners

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

interface BowlerActionListener {
    fun throwBall(pinKnockedCount: Int)
    fun onFrameSelected(frameIndex: Int)
    fun clearScore()
}