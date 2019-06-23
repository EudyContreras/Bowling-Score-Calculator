package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager
import com.eudycontreras.bowlingcalculator.persistance.dao.BowlersDao
import com.eudycontreras.bowlingcalculator.persistance.entities.BowlerEntity
import com.eudycontreras.bowlingcalculator.utilities.extensions.switchMap
import com.eudycontreras.bowlingcalculator.utilities.fromIO

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class BowlerRepositoryImpl(
    private val manager: PersistenceManager,
    private val bowlerDao: BowlersDao
) : BowlerRepository {

    @WorkerThread
    override fun saveBowler(bowler: Bowler) {
        val id = bowlerDao.insert(BowlerEntity.from(bowler))
        bowler.id = id
        bowler.frames.forEach {
            it.bowlerId = id
        }
    }

    @WorkerThread
    override fun saveBowlers(bowlers: List<Bowler>) {
        bowlers.forEach {
           saveBowler(it)
        }
    }

    @WorkerThread
    override fun updateBowler(bowler: Bowler) {
        bowlerDao.replace(BowlerEntity.from(bowler))
    }

    @WorkerThread
    override fun bowlerExists(bowlerId: Long): Boolean {
        return bowlerDao.exists(bowlerId)
    }

    @WorkerThread
    override fun getBowlerCount(): Int {
        return bowlerDao.getCount()
    }

    @WorkerThread
    override fun getBowlerCount(resultId: Long): Int {
        return bowlerDao.getCount(resultId)
    }

    override fun getBowlers(bowlerIds: LongArray): LiveData<List<Bowler>> {
        val bowlers = MutableLiveData<List<Bowler>>()
        fromIO {
            val temp = arrayListOf<Bowler>()
            for (id in bowlerIds) {
                val exists = bowlerDao.exists(id)
                if (!exists)
                    continue

                val bowler = bowlerDao.getById(id).toBowler()
                bowler.frames = manager.frameRepo.getFrames(bowler)
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