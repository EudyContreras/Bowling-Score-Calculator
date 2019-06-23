package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Result

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface ResultRepository {

    fun saveResult(result: Result, listener: ((id: Long, name: String) -> Unit)? = null)

    fun updateResult(result: Result)

    fun saveResults(results: List<Result>)

    fun getResult(resultId: Long): LiveData<Result>

    fun getResults(): LiveData<List<Result>>

    fun deleteResult(result: Result)

    fun deleteAll()
}