package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Result

/**
 * Created by eudycontreras.
 */

interface ResultRepository {

    fun saveResult(result: Result)

    fun updateResult(result: Result)

    fun saveResults(results: List<Result>)

    fun getResults(): LiveData<List<Result>>

    fun deleteResult(result: Result)

    fun deleteAll()
}