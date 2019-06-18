package com.eudycontreras.bowlingcalculator.repositories

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll

/**
 * Created by eudycontreras.
 */

interface RollRepository {

    fun saveRolls(rolls: List<Roll>)

    fun getRolls(bowler: Bowler): List<Roll>

    fun getRolls(frame: Frame): List<Roll>

    fun getRolls(frames: List<Frame>): List<Roll>

    fun updateRolls(frame: Frame, rolls: List<Roll>)

    fun delete(bowler: Bowler)

    fun delete(frame: Frame)

    fun deleteAll()
}