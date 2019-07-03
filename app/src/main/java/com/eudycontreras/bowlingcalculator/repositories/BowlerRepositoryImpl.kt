package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.MainThread
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class BowlerRepositoryImpl(
    private val manager: PersistenceManager,
    private val bowlerDao: BowlersDao
) : BowlerRepository {

    @WorkerThread
    override suspend fun saveBowler(bowler: Bowler) {
        val id = bowlerDao.insert(BowlerEntity.from(bowler))
        bowler.id = id
        bowler.frames.forEach {
            it.bowlerId = id
        }
    }

    @WorkerThread
    override suspend fun saveBowlers(bowlers: List<Bowler>) {
        bowlers.forEach {
           saveBowler(it)
        }
    }

    @WorkerThread
    override suspend fun updateBowler(bowler: Bowler) {
        bowlerDao.replace(BowlerEntity.from(bowler))
    }

    @WorkerThread
    override suspend fun bowlerExists(bowlerId: Long): Boolean {
        return bowlerDao.exists(bowlerId)
    }

    @WorkerThread
    override suspend fun getBowlerCount(): Int {
        return bowlerDao.getCount()
    }

    @WorkerThread
    override suspend fun getBowlerCount(resultId: Long): Int {
        return bowlerDao.getCount(resultId)
    }

    @WorkerThread
    override suspend fun getBowlers(result: Result): List<Bowler> {
        return bowlerDao.findByResultId(result.id).map { it.toBowler() }
    }

    @MainThread
    override fun getBowlers(bowlerIds: LongArray): LiveData<List<Bowler>> {
        val bowlers = MutableLiveData<List<Bowler>>()
        GlobalScope.launch(Dispatchers.IO) {
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

    @MainThread
    override fun getDefaultBowler(): LiveData<Bowler> {
        return bowlerDao.getDefault().switchMap {
            val data = MediatorLiveData<Bowler>()
            data.value = it.toBowler()
            return@switchMap data
        }
    }

    @WorkerThread
    override suspend fun deleteBowler(bowler: Bowler) {
        bowlerDao.deleteById(bowler.id)
    }

    @WorkerThread
    override suspend fun deleteAll(result: Result) {
        bowlerDao.deleteByResultId(result.id)
    }

    @WorkerThread
    override suspend fun deleteAll() {
        bowlerDao.clear()
    }
}