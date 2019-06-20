package com.eudycontreras.bowlingcalculator.repositories

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame

/**
 * Created by eudycontreras.
 */

interface FrameRepository {

    fun saveFrames(frames: List<Frame>)

    fun updateFrames(bowlerId: Long, frames: List<Frame>)

    fun getFrames(bowler: Bowler): List<Frame>

    fun deleteFrames(bowler: Bowler)

    fun deleteAll()
}