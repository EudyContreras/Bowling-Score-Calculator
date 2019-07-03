package com.eudycontreras.bowlingcalculator.repositories

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface FrameRepository {

    suspend fun saveFrames(frames: List<Frame>)

    suspend fun updateFrames(bowlerId: Long, frames: List<Frame>)

    suspend fun getFrames(bowler: Bowler): List<Frame>

    suspend fun deleteFrames(bowler: Bowler)

    suspend fun deleteAll()
}