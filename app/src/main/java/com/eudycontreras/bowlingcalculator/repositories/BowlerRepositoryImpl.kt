package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.eudycontreras.bowlingcalculator.Application
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.extensions.switchMap
import com.eudycontreras.bowlingcalculator.persistance.dao.BowlersDao
import com.eudycontreras.bowlingcalculator.persistance.entities.BowlerEntity
import com.eudycontreras.bowlingcalculator.utilities.AppExecutors

/**
 * Created by eudycontreras.
 */

class BowlerRepositoryImpl(
    application: Application,
    private val bowlerDao: BowlersDao
) : BowlerRepository {

    private val appExecutor: AppExecutors = application.appExecutor

    @WorkerThread
    override fun saveBowler(bowler: Bowler) {
        val id = bowlerDao.insert(BowlerEntity.from(bowler))
        bowler.id = id
    }

    @WorkerThread
    override fun updateBowler(bowler: Bowler) {
        bowlerDao.update(BowlerEntity.from(bowler))
    }

    @WorkerThread
    override fun saveBowlers(bowlers: List<Bowler>) {
        bowlerDao.insert(bowlers.map { BowlerEntity.from(it) })
    }

    override fun getBowlers(result: Result): LiveData<List<Bowler>> {
        return bowlerDao.findByResultId(result.id).switchMap { entities ->
            val data = MediatorLiveData<List<Bowler>>()
            data.value = entities.map { it.toBowler() }
            return@switchMap data
        }
    }

    override fun getDefaultBowler(): LiveData<Bowler> {
        return bowlerDao.getDefault().switchMap {
            val data = MediatorLiveData<Bowler>()
            data.value = it.toBowler()
            return@switchMap data
        }
    }

    @WorkerThread
    override fun deleteBowler(bowler: Bowler) {
        bowlerDao.deleteById(bowler.id)
    }

    @WorkerThread
    override fun deleteAll(result: Result) {
        bowlerDao.deleteByResultId(result.id)
    }

    @WorkerThread
    override fun deleteAll() {
        bowlerDao.clear()
    }
}