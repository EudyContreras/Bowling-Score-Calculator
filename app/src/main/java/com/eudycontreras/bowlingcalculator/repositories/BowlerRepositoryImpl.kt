package com.eudycontreras.bowlingcalculator.repositories

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

    override fun saveBowler(bowler: Bowler) {
        appExecutor.ioThread {
            bowlerDao.insert(BowlerEntity.from(bowler))
        }
    }

    override fun updateBowler(bowler: Bowler) {
        appExecutor.ioThread {
            bowlerDao.update(BowlerEntity.from(bowler))
        }
    }

    override fun saveBowlers(bowlers: List<Bowler>) {
        appExecutor.ioThread {
            bowlerDao.insert(bowlers.map { BowlerEntity.from(it) })
        }
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

    override fun deleteBowler(bowler: Bowler) {
        appExecutor.ioThread {
            bowlerDao.deleteById(bowler.id)
        }
    }

    override fun deleteAll(result: Result) {
        appExecutor.ioThread {
            bowlerDao.deleteByResultId(result.id)
        }
    }

    override fun deleteAll() {
        appExecutor.ioThread {
            bowlerDao.clear()
        }
    }
}