package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.eudycontreras.bowlingcalculator.Application
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.extensions.switchMap
import com.eudycontreras.bowlingcalculator.persistance.dao.FramesDao
import com.eudycontreras.bowlingcalculator.persistance.entities.FrameEntity
import com.eudycontreras.bowlingcalculator.utilities.AppExecutors

/**
 * Created by eudycontreras.
 */

class FrameRepositoryImpl(
    application: Application,
    private val frameDao: FramesDao
) : FrameRepository {

    private val appExecutor: AppExecutors = application.appExecutor

    @WorkerThread
    override fun saveFrames(frames: List<Frame>) {
        frameDao.insert(frames.map { FrameEntity.from(it) })
    }

    @WorkerThread
    override fun updateFrames(bower: Bowler, frames: List<Frame>) {
        frameDao.update(frames.map { FrameEntity.from(it) })
    }

    override fun getFrames(bower: Bowler): LiveData<List<Frame>> {
        return frameDao.findForBowler(bower.id).switchMap { entities ->
            val data = MediatorLiveData<List<Frame>>()
            data.value = entities.map { it.toFrame() }
            return@switchMap data
        }
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