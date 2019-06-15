package com.eudycontreras.bowlingcalculator.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.eudycontreras.bowlingcalculator.Application
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll
import com.eudycontreras.bowlingcalculator.extensions.switchMap
import com.eudycontreras.bowlingcalculator.persistance.dao.RollsDao
import com.eudycontreras.bowlingcalculator.persistance.entities.RollEntity
import com.eudycontreras.bowlingcalculator.utilities.AppExecutors

/**
 * Created by eudycontreras.
 */

class RollRepositoryImpl(
    application: Application,
    private val rollDao: RollsDao
) : RollRepository {

    private val appExecutor: AppExecutors = application.appExecutor

    override fun saveRolls(rolls: List<Roll>) {
        appExecutor.ioThread {
            rollDao.insert(rolls.map { RollEntity.from(it) })
        }
    }

    override fun updateRolls(frame: Frame, rolls: List<Roll>) {
        appExecutor.ioThread {
            rollDao.update(rolls.map { RollEntity.from(it) })
        }
    }

    override fun getRolls(bowler: Bowler): LiveData<List<Roll>> {
        return rollDao.findByBowlerId(bowler.id).switchMap { entities ->
            val data = MediatorLiveData<List<Roll>>()
            data.value = entities.map { it.toRoll() }
            return@switchMap data
        }
    }

    override fun getRolls(frame: Frame): LiveData<List<Roll>> {
        return rollDao.findByBowlerIdAndIndex(frame.bowlerId, frame.index).switchMap { entities ->
            val data = MediatorLiveData<List<Roll>>()
            data.value = entities.map { it.toRoll() }
            return@switchMap data
        }
    }

    override fun getRolls(frames: List<Frame>): LiveData<List<Roll>> {
        val liveData: MediatorLiveData<List<Roll>> = MediatorLiveData()
        appExecutor.ioThread {
            val rolls: ArrayList<Roll> = ArrayList()

            for (frame in frames) {
                val dbRolls = rollDao.getByBowlerIdAndIndex(frame.bowlerId, frame.index).map { it.toRoll() }
                rolls.addAll(dbRolls)
            }

            appExecutor.mainThread {
                liveData.value = rolls
            }
        }
        return liveData
    }

    override fun delete(bowler: Bowler) {
        appExecutor.ioThread {
            rollDao.delete(bowler.id)
        }
    }

    override fun delete(frame: Frame) {
        appExecutor.ioThread {
            rollDao.delete(frame.index, frame.bowlerId)
        }
    }

    override fun deleteAll() {
        appExecutor.ioThread {
            rollDao.clear()
        }
    }

}