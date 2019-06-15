package com.eudycontreras.bowlingcalculator.repositories

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

    override fun saveFrames(frames: List<Frame>) {
        appExecutor.ioThread {
            frameDao.insert(frames.map { FrameEntity.from(it) })
        }
    }

    override fun updateFrames(bower: Bowler, frames: List<Frame>) {
        appExecutor.ioThread {
            frameDao.update(frames.map { FrameEntity.from(it) })
        }
    }

    override fun getFrames(bower: Bowler): LiveData<List<Frame>> {
        return frameDao.findForBowler(bower.id).switchMap { entities ->
            val data = MediatorLiveData<List<Frame>>()
            data.value = entities.map { it.toFrame() }
            return@switchMap data
        }
    }

    override fun deleteFrames(bowler: Bowler) {
        appExecutor.ioThread {
            frameDao.delete(bowler.id)
        }
    }

    override fun deleteAll() {
        appExecutor.ioThread {
            frameDao.clear()
        }
    }
}