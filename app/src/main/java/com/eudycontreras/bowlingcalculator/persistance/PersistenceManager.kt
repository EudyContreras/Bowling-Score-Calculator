package com.eudycontreras.bowlingcalculator.persistance

import androidx.lifecycle.LiveData
import androidx.room.withTransaction
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class PersistenceManager(
    application: Application
) {
    private val appDatabase: AppDatabase = AppDatabase.getInstance(application)

    private val storage: PrimitiveStorage = PrimitiveStorageImpl(application)

    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    val bowlerRepo = BowlerRepositoryImpl(this, appDatabase.bowler)
    val frameRepo = FrameRepositoryImpl(this, appDatabase.frame)
    val resultRepo = ResultRepositoryImpl(this, appDatabase.result)
    val rollRepo = RollRepositoryImpl(this, appDatabase.roll)

    fun updateBowler(bowler: Bowler, onEnd: (() -> Unit)? = null) = ioScope.launch {
        appDatabase.withTransaction {
            bowlerRepo.updateBowler(bowler)
            frameRepo.updateFrames(bowler.id, bowler.frames)
            rollRepo.updateRolls(bowler.id, bowler.frames.flatMap { it.rolls.values })
        }

        launch(Dispatchers.Main.immediate) {
            onEnd?.invoke()
        }
    }

    fun updateBowlers(bowlers: List<Bowler>, onEnd: (() -> Unit)? = null) = ioScope.launch {
        appDatabase.withTransaction {
            for (bowler in bowlers) {
                bowlerRepo.updateBowler(bowler)
                frameRepo.updateFrames(bowler.id, bowler.frames)
                rollRepo.updateRolls(bowler.id, bowler.frames.flatMap { it.rolls.values })
            }
        }

        launch(Dispatchers.Main.immediate) {
            onEnd?.invoke()
        }
    }

    fun resetBowler(bowler: Bowler, onEnd: ((Bowler) -> Unit)? = null) = ioScope.launch {
        appDatabase.withTransaction {
            bowlerRepo.updateBowler(bowler)
            frameRepo.updateFrames(bowler.id, bowler.frames)
            rollRepo.delete(bowler)
        }

        launch(Dispatchers.Main.immediate) {
            onEnd?.invoke(bowler)
        }
    }

    fun saveBowlers(bowlers: List<Bowler>, listener: BowlerListener = null) = ioScope.launch {
        appDatabase.withTransaction {
            bowlerRepo.saveBowlers(bowlers)
            for (bowler in bowlers) {
                frameRepo.saveFrames(bowler.frames)
                if (bowler.hasStarted()) {
                    rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
                }
            }
        }

        saveActiveBowlersIds(bowlers.map { it.id }.toLongArray())

        launch(Dispatchers.Main) {
            listener?.invoke(bowlers)
        }
    }

    fun saveResult(result: Result, listener: ((name: String) -> Unit)? = null) = ioScope.launch {
        appDatabase.withTransaction {
            resultRepo.saveResult(result)
            for (bowler in result.bowlers) {
                bowler.resultId = result.id
                bowlerRepo.saveBowler(bowler)
                frameRepo.saveFrames(bowler.frames)
                if (bowler.hasStarted()) {
                    rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
                }
            }
        }

        launch(Dispatchers.Main.immediate) {
            listener?.invoke(result.name)
            resultRepo.getResults().observeForever {

            }
        }
    }

    fun removeBowler(bowler: Bowler, function: ((Bowler) -> Unit)?) = ioScope.launch {

        val filtered = storage.currentBowlerIds.filter { id -> id != bowler.id }

        appDatabase.withTransaction {
            rollRepo.delete(bowler)
            frameRepo.deleteFrames(bowler)
            bowlerRepo.deleteBowler(bowler)
        }

        saveActiveBowlersIds(filtered.toLongArray())

        launch(Dispatchers.Main.immediate) {
            function?.invoke(bowler)
        }
    }

    fun getBowlers(): LiveData<List<Bowler>> {
        val bowlerIds = storage.currentBowlerIds
        return  bowlerRepo.getBowlers(bowlerIds)
    }

    fun getResult(resultId: Long): LiveData<Result> {
        return resultRepo.getResult(resultId)
    }

    @Synchronized fun saveActiveTab(activeTab: Int): Int {
        storage.activeTab = activeTab
        return activeTab
    }

    @Synchronized fun saveActiveBowlersIds(bowlerIds: LongArray): LongArray {
        val storedIds = storage.currentBowlerIds.toMutableList().plus(bowlerIds.toList()).distinct().toLongArray()
        storage.currentBowlerIds = storedIds
        return storedIds
    }

    fun hasBowlers(): Boolean {
        return storage.currentBowlerIds.isNotEmpty()
    }

    fun getActiveTab(): Int = storage.activeTab
}