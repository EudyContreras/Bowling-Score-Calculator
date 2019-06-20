package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.eudycontreras.bowlingcalculator.Application
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.extensions.switchMap
import com.eudycontreras.bowlingcalculator.persistance.dao.ResultsDao
import com.eudycontreras.bowlingcalculator.persistance.entities.ResultEntity

/**
 * Created by eudycontreras.
 */

class ResultRepositoryImpl(
    private val application: Application,
    private val resultDao: ResultsDao
) : ResultRepository {

    @WorkerThread
    override fun saveResult(result: Result, listener: ((id: Long, name: String) -> Unit)?) {
        resultDao.insert(ResultEntity.from(result))
        listener?.invoke(result.id, result.name)
    }

    @WorkerThread
    override fun updateResult(result: Result) {
        resultDao.update(ResultEntity.from(result))
    }

    @WorkerThread
    override fun saveResults(results: List<Result>) {
        resultDao.insert(results.map { ResultEntity.from(it) })
    }

    override fun getResults(): LiveData<List<Result>> {
        return resultDao.findAllOrderByDate().switchMap { entities ->
            val data = MediatorLiveData<List<Result>>()
            data.value = entities.map { it.toResult() }
            return@switchMap data
        }
    }

    @WorkerThread
    override fun deleteResult(result: Result) {
        resultDao.delete(result.id)
    }

    @WorkerThread
    override fun deleteAll() {
        resultDao.clear()
    }
}