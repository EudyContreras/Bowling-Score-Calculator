package com.eudycontreras.bowlingcalculator.repositories

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
interface FrameRepository {

    suspend fun saveFrames(frames: List<Frame>)

    suspend fun updateFrames(bowlerId: Long, frames: List<Frame>)

    suspend fun getFrames(bowler: Bowler): List<Frame>

    suspend fun deleteFrames(bowler: Bowler)

    suspend fun deleteAll()
}