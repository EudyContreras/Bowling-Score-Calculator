package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Result

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface BowlerRepository {

    suspend fun saveBowler(bowler: Bowler)

    suspend fun saveBowlers(bowlers: List<Bowler>)

    suspend fun updateBowler(bowler: Bowler)

    suspend fun bowlerExists(bowlerId: Long): Boolean

    suspend fun getBowlerCount(): Int

    suspend fun getBowlerCount(resultId: Long): Int

    suspend fun getBowlers(result: Result): List<Bowler>

    fun getBowlers(bowlerIds: LongArray): LiveData<List<Bowler>>

    fun getDefaultBowler(): LiveData<Bowler>

    suspend fun deleteBowler(bowler: Bowler)

    suspend fun deleteAll(result: Result)

    suspend fun deleteAll()

}