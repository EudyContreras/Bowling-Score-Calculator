package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager
import com.eudycontreras.bowlingcalculator.persistance.dao.RollsDao
import com.eudycontreras.bowlingcalculator.persistance.entities.RollEntity

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class RollRepositoryImpl(
    manager: PersistenceManager,
    private val rollDao: RollsDao
) : RollRepository {

    @WorkerThread
    override fun saveRolls(rolls: List<Roll>) {
        rollDao.insert(rolls.map { RollEntity.from(it) })
    }

    @WorkerThread
    override fun updateRolls(rolls: List<Roll>) {
        rollDao.update(rolls.map { RollEntity.from(it) })
    }

    @WorkerThread
    override fun updateRolls(bowlerId: Long, rolls: List<Roll>) {
        rollDao.replaceAllFor(bowlerId, rolls.map { RollEntity.from(it) })
    }

    @WorkerThread
    override fun getRolls(bowler: Bowler): List<Roll> {
        return rollDao.findByBowlerId(bowler.id).map { it.toRoll() }
    }

    @WorkerThread
    override fun getRolls(frame: Frame): List<Roll> {
        return rollDao.findByBowlerIdAndIndex(frame.bowlerId, frame.index).map { it.toRoll() }
    }

    @WorkerThread
    override fun getRolls(frames: List<Frame>): List<Roll> {
        val rolls: ArrayList<Roll> = ArrayList()

        for (frame in frames) {
            val dbRolls = rollDao.getByBowlerIdAndIndex(frame.bowlerId, frame.index).map { it.toRoll() }
            rolls.addAll(dbRolls)
        }

        return rolls
    }

    @WorkerThread
    override fun delete(bowler: Bowler) {
        rollDao.delete(bowler.id)
    }

    @WorkerThread
    override fun delete(frame: Frame) {
        rollDao.delete(frame.index, frame.bowlerId)
    }

    @WorkerThread
    override fun deleteAll() {
        rollDao.clear()
    }
}