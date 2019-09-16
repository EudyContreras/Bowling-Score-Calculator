package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager
import com.eudycontreras.bowlingcalculator.persistance.dao.FramesDao
import com.eudycontreras.bowlingcalculator.persistance.entities.FrameEntity

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
class FrameRepositoryImpl(
    private val manager: PersistenceManager,
    private val frameDao: FramesDao
) : FrameRepository {

    @WorkerThread
    override suspend fun saveFrames(frames: List<Frame>) {
        frameDao.insert(frames.map { FrameEntity.from(it) })
    }

    @WorkerThread
    override suspend fun updateFrames(bowlerId: Long, frames: List<Frame>) {
        frameDao.replaceFor(bowlerId, frames.map { FrameEntity.from(it) })
    }

    @WorkerThread
    override suspend fun getFrames(bowler: Bowler): List<Frame> {
        val frames = frameDao.findForBowler(bowler.id).map { it.toFrame() }
        frames.forEach {
            it.bowlerId = bowler.id
            val rolls = manager.rollRepo.getRolls(it)
            rolls.sortedBy { roll -> roll.parentState.ordinal }.forEach { roll ->
                it.rolls[roll.parentState] = roll
            }
        }
        return frames
    }

    @WorkerThread
    override suspend fun deleteFrames(bowler: Bowler) {
        frameDao.delete(bowler.id)
    }

    @WorkerThread
    override suspend fun deleteAll() {
        frameDao.clear()
    }
}