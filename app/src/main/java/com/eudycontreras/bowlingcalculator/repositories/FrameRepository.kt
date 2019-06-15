package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame

/**
 * Created by eudycontreras.
 */

interface FrameRepository {

    fun saveFrames(frames: List<Frame>)

    fun updateFrames(bower: Bowler, frames: List<Frame>)

    fun getFrames(bower: Bowler): LiveData<List<Frame>>

    fun deleteFrames(bowler: Bowler)

    fun deleteAll()
}