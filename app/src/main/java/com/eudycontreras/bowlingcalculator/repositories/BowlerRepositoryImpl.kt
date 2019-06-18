package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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
    private val application: Application,
    private val bowlerDao: BowlersDao
) : BowlerRepository {

    private val appExecutor: AppExecutors = application.appExecutor

    @WorkerThread
    override fun saveBowler(bowler: Bowler) {
        val id = bowlerDao.insert(BowlerEntity.from(bowler))
        bowler.id = id
        bowler.frames.forEach {
            it.bowlerId = id
        }
    }

    @WorkerThread
    override fun updateBowler(bowler: Bowler) {
        bowlerDao.update(BowlerEntity.from(bowler))
    }

    @WorkerThread
    override fun saveBowlers(bowlers: List<Bowler>) {
        bowlers.forEach {
           saveBowler(it)
        }
    }

    override fun getBowlers(bowlerIds: LongArray): LiveData<List<Bowler>> {
        val bowlers = MutableLiveData<List<Bowler>>()
        appExecutor.ioThread {
            val temp = arrayListOf<Bowler>()
            bowlerIds.forEach {
                val bowler = bowlerDao.getById(it).toBowler()
                bowler.frames = application.frameRepo.getFrames(bowler)
                temp.add(bowler)
            }
            bowlers.postValue(temp)
        }
        return bowlers
    }

    override fun getDefaultBowler(): LiveData<Bowler> {
        return bowlerDao.getDefault().switchMap {
            val data = MediatorLiveData<Bowler>()
            data.value = it.toBowler()
            return@switchMap data
        }
    }

    @WorkerThread
    override fun getBowlers(result: Result): List<Bowler> {
        return bowlerDao.findByResultId(result.id).map { it.toBowler() }
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