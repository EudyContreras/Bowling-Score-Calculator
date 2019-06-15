package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.eudycontreras.bowlingcalculator.Application
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.extensions.switchMap
import com.eudycontreras.bowlingcalculator.persistance.dao.ResultsDao
import com.eudycontreras.bowlingcalculator.persistance.entities.ResultEntity
import com.eudycontreras.bowlingcalculator.utilities.AppExecutors

/**
 * Created by eudycontreras.
 */

class ResultRepositoryImpl(
    application: Application,
    private val resultDao: ResultsDao
) : ResultRepository {

    private val appExecutor: AppExecutors = application.appExecutor

    override fun saveResult(result: Result) {
        appExecutor.ioThread {
            resultDao.insert(ResultEntity.from(result))
        }
    }

    override fun updateResult(result: Result) {
        appExecutor.ioThread {
            resultDao.update(ResultEntity.from(result))
        }
    }

    override fun saveResults(results: List<Result>) {
        appExecutor.ioThread {
            resultDao.insert(results.map { ResultEntity.from(it) })
        }
    }

    override fun getResults(): LiveData<List<Result>> {
        return resultDao.findAllOrderByDate().switchMap { entities ->
            val data = MediatorLiveData<List<Result>>()
            data.value = entities.map { it.toResult() }
            return@switchMap data
        }
    }

    override fun deleteResult(result: Result) {
        appExecutor.ioThread {
            resultDao.delete(result.id)
        }
    }

    override fun deleteAll() {
        appExecutor.ioThread {
            resultDao.clear()
        }
    }

}