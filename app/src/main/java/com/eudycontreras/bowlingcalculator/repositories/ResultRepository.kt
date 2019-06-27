package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Result

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface ResultRepository {

    suspend fun saveResult(result: Result)

    suspend fun updateResult(result: Result)

    suspend fun saveResults(results: List<Result>)

    fun getResult(resultId: Long): LiveData<Result>

    fun getResults(): LiveData<List<Result>>

    suspend fun deleteResult(result: Result)

    suspend fun deleteAll()
}