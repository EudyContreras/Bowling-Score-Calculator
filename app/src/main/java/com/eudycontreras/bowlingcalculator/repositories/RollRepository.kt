package com.eudycontreras.bowlingcalculator.repositories

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll


/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
interface RollRepository {

    suspend fun saveRolls(rolls: List<Roll>)

    suspend fun updateRolls(rolls: List<Roll>)

    suspend fun updateRolls(bowlerId: Long, rolls: List<Roll>)

    suspend fun getRolls(bowler: Bowler): List<Roll>

    suspend fun getRolls(frame: Frame): List<Roll>

    suspend fun getRolls(frames: List<Frame>): List<Roll>

    suspend fun delete(bowler: Bowler)

    suspend fun delete(frame: Frame)

    suspend fun deleteAll()
}