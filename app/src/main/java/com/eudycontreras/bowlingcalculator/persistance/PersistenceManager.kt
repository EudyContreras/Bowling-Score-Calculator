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
import com.eudycontreras.bowlingcalculator.utilities.extensions.fromMain
import com.eudycontreras.bowlingcalculator.utilities.fromScopeIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun updateBowler(bowler: Bowler, onEnd: (() -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.IO) {
            bowlerRepo.updateBowler(bowler)
            frameRepo.updateFrames(bowler.id, bowler.frames)
            rollRepo.updateRolls(bowler.id, bowler.frames.flatMap { it.rolls.values })

            withContext(Dispatchers.Main.immediate) {
                onEnd?.invoke()
            }
        }
    }

    fun resetBowler(bowler: Bowler, onEnd: ((Bowler) -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.IO) {
            bowlerRepo.updateBowler(bowler)
            frameRepo.updateFrames(bowler.id, bowler.frames)
            rollRepo.delete(bowler)

            withContext(Dispatchers.Main.immediate) {
                onEnd?.invoke(bowler)
            }
        }
    }

    fun saveBowlers(bowlers: List<Bowler>, listener: BowlerListener = null) {
        GlobalScope.launch(Dispatchers.IO) {
            bowlerRepo.saveBowlers(bowlers)

            for (bowler in bowlers) {
                frameRepo.saveFrames(bowler.frames)
                rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
            }

            saveActiveBowlersIds(bowlers.map { it.id }.toLongArray())

            withContext(Dispatchers.Main.immediate) {
                listener?.invoke(bowlers)
            }
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
                it.fromMain {
                    listener?.invoke(name)
                    resultRepo.getResults().observeForever {

                    }
                }
            }
        }
    }

    fun removeBowler(bowler: Bowler, function: ((Bowler) -> Unit)?) {
        GlobalScope.launch(Dispatchers.IO) {

            val filtered = storage.currentBowlerIds.filter { id -> id != bowler.id }

            rollRepo.delete(bowler)
            frameRepo.deleteFrames(bowler)
            bowlerRepo.deleteBowler(bowler)

            saveActiveBowlersIds(filtered.toLongArray())

            withContext(Dispatchers.Main.immediate) {
                function?.invoke(bowler)
            }
        }
    }

    @Synchronized fun saveActiveTab(activeTab: Int): Int {
        storage.activeTab = activeTab
        return activeTab
    }

    @Synchronized fun saveActiveBowlersIds(bowlerIds: LongArray): LongArray {
        storage.currentBowlerIds = bowlerIds
        return bowlerIds
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

    fun getActiveTab(): Int = storage.activeTab
}