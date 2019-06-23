package com.eudycontreras.bowlingcalculator.persistance

import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.Application
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.persistance.primitive.PrimitiveStorage
import com.eudycontreras.bowlingcalculator.persistance.primitive.PrimitiveStorageImpl
import com.eudycontreras.bowlingcalculator.repositories.BowlerRepositoryImpl
import com.eudycontreras.bowlingcalculator.repositories.FrameRepositoryImpl
import com.eudycontreras.bowlingcalculator.repositories.ResultRepositoryImpl
import com.eudycontreras.bowlingcalculator.repositories.RollRepositoryImpl
import com.eudycontreras.bowlingcalculator.utilities.BowlerListener
import com.eudycontreras.bowlingcalculator.utilities.fromIO
import com.eudycontreras.bowlingcalculator.utilities.fromMain
import com.eudycontreras.bowlingcalculator.utilities.fromScopeIO

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class PersistenceManager(
    application: Application
) {
    private val appDatabase: AppDatabase = AppDatabase.getInstance(application)

    private val storage: PrimitiveStorage = PrimitiveStorageImpl(application)

    val bowlerRepo = BowlerRepositoryImpl(this, appDatabase.bowler)
    val frameRepo = FrameRepositoryImpl(this, appDatabase.frame)
    val resultRepo = ResultRepositoryImpl(this, appDatabase.result)
    val rollRepo = RollRepositoryImpl(this, appDatabase.roll)

    val activeTab: Int
        get() = storage.activeTab

    fun updateBowler(bowler: Bowler, onEnd: (() -> Unit)? = null) {
        fromIO {
            bowlerRepo.updateBowler(bowler)
            frameRepo.updateFrames(bowler.id, bowler.frames)
            rollRepo.updateRolls(bowler.id, bowler.frames.flatMap { it.rolls.values })
            fromMain(onEnd)
        }
    }

    fun resetBowler(bowler: Bowler, onEnd: (() -> Unit)? = null) {
        fromIO {
            bowlerRepo.updateBowler(bowler)
            frameRepo.updateFrames(bowler.id, bowler.frames)
            rollRepo.delete(bowler)
            fromMain(onEnd)
        }
    }

    fun saveBowlers(bowlers: List<Bowler>, listener: BowlerListener = null) {
        fromIO {
            bowlerRepo.saveBowlers(bowlers)
            storage.currentBowlerIds = bowlers.map { it.id }.toLongArray()
            for (bowler in bowlers) {
                frameRepo.saveFrames(bowler.frames)
                rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
            }
            fromMain(bowlers, listener)
        }
    }

    fun saveResult(result: Result, listener: ((name: String) -> Unit)? = null) {
        fromScopeIO {
            resultRepo.saveResult(result) { id, name ->
                for (bowler in result.bowlers) {
                    bowler.resultId = id
                    bowlerRepo.saveBowler(bowler)
                    frameRepo.saveFrames(bowler.frames)
                    rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
                }
                fromMain {
                    listener?.invoke(name)
                    resultRepo.getResults().observeForever {
                        var results = it
                    }
                }
            }
        }
    }

    fun removeBowler(bowler: Bowler, function: (() -> Unit)?) {
        fromIO {
            val filtered = storage.currentBowlerIds.filter { it != bowler.id }
            storage.currentBowlerIds = filtered.toLongArray()
            rollRepo.delete(bowler)
            frameRepo.deleteFrames(bowler)
            bowlerRepo.deleteBowler(bowler)
            fromMain(function)
        }
    }

    fun saveActiveTab(activeTab: Int) {
        storage.activeTab = activeTab
    }

    fun getBowlers(): LiveData<List<Bowler>> {
        val bowlerIds = storage.currentBowlerIds
        return bowlerRepo.getBowlers(bowlerIds)
    }

    fun getResult(resultId: Long): LiveData<Result> {
        return resultRepo.getResult(resultId)
    }

    fun hasBowlers(): Boolean {
        return storage.currentBowlerIds.isNotEmpty()
    }
}