package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager
import com.eudycontreras.bowlingcalculator.persistance.dao.ResultsDao
import com.eudycontreras.bowlingcalculator.persistance.entities.ResultEntity
import com.eudycontreras.bowlingcalculator.utilities.extensions.switchMap

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
class ResultRepositoryImpl(
    private val manager: PersistenceManager,
    private val resultDao: ResultsDao
) : ResultRepository {

    @WorkerThread
    override suspend fun saveResult(result: Result) {
        result.id = resultDao.insert(ResultEntity.from(result))
    }

    @WorkerThread
    override suspend fun updateResult(result: Result) {
        resultDao.update(ResultEntity.from(result))
    }

    @WorkerThread
    override suspend fun saveResults(results: List<Result>) {
        resultDao.insert(results.map { ResultEntity.from(it) })
    }

    @MainThread
    override fun getResult(resultId: Long): LiveData<Result> {
        return resultDao.findById(resultId).switchMap {
            val data = MediatorLiveData<Result>()
            data.value = it.toResult()
            return@switchMap data
        }
    }

    @MainThread
    override fun getResults(): LiveData<List<Result>> {
        return resultDao.findAllOrderByDate().switchMap { entities ->
            val data = MediatorLiveData<List<Result>>()
            data.value = entities.map { it.toResult() }
            return@switchMap data
        }
    }

    @WorkerThread
    override suspend fun deleteResult(result: Result) {
        resultDao.delete(result.id)
    }

    @WorkerThread
    override suspend fun deleteAll() {
        resultDao.clear()
    }
}