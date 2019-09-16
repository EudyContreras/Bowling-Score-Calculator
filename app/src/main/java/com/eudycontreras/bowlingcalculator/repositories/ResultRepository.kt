package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Result

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
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