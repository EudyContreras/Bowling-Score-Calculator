package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager
import com.eudycontreras.bowlingcalculator.persistance.dao.FramesDao
import com.eudycontreras.bowlingcalculator.persistance.entities.FrameEntity

/**
 * Created by eudycontreras.
 */

class FrameRepositoryImpl(
    private val manager: PersistenceManager,
    private val frameDao: FramesDao
) : FrameRepository {

    @WorkerThread
    override fun saveFrames(frames: List<Frame>) {
        frameDao.insert(frames.map { FrameEntity.from(it) })
    }

    @WorkerThread
    override fun updateFrames(bowlerId: Long, frames: List<Frame>) {
        frameDao.replaceFor(bowlerId, frames.map { FrameEntity.from(it) })
    }

    @WorkerThread
    override fun getFrames(bowler: Bowler): List<Frame> {
        val frames = frameDao.findForBowler(bowler.id).map { it.toFrame() }
        frames.forEach {
            it.bowlerId = bowler.id
            val rolls = manager.rollRepo.getRolls(it)
            rolls.forEach { roll ->
                it.rolls[roll.parentState] = roll
            }
        }
        return frames
    }

    @WorkerThread
    override fun deleteFrames(bowler: Bowler) {
        frameDao.delete(bowler.id)
    }

    @WorkerThread
    override fun deleteAll() {
        frameDao.clear()
    }
}