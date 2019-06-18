package com.eudycontreras.bowlingcalculator

import android.app.Application
import androidx.lifecycle.LiveData
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.persistance.AppDatabase
import com.eudycontreras.bowlingcalculator.persistance.primitive.PrimitiveStorage
import com.eudycontreras.bowlingcalculator.persistance.primitive.PrimitiveStorageImpl
import com.eudycontreras.bowlingcalculator.repositories.*
import com.eudycontreras.bowlingcalculator.utilities.AppExecutors

/**
 * Created by eudycontreras.
 */
class Application : Application() {

    private lateinit var appDatabase: AppDatabase

    lateinit var storage: PrimitiveStorage

    lateinit var appExecutor: AppExecutors

    lateinit var bowlerRepo: BowlerRepository
    lateinit var frameRepo: FrameRepository
    lateinit var resultRepo: ResultRepository
    lateinit var rollRepo: RollRepository

    override fun onCreate() {
        super.onCreate()

        appExecutor = AppExecutors.instance()
        appDatabase = AppDatabase.getInstance(this)
        storage = PrimitiveStorageImpl(this)

        initializeRepositories(appDatabase)
    }

    private fun initializeRepositories(appDatabase: AppDatabase) {
        bowlerRepo = BowlerRepositoryImpl(this, appDatabase.bowler)
        frameRepo = FrameRepositoryImpl(this, appDatabase.frame)
        resultRepo = ResultRepositoryImpl(this, appDatabase.result)
        rollRepo = RollRepositoryImpl(this, appDatabase.roll)
    }

     fun saveBowler(bowler: Bowler, listener: ((bowler: Bowler) -> Unit)? = null) {
        appExecutor.ioThread {
            bowlerRepo.saveBowler(bowler)
            frameRepo.saveFrames(bowler.frames)
            rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
            appExecutor.mainThread {
                listener?.invoke(bowler)
            }
        }
    }

    fun saveBowlers(bowlers: List<Bowler>, listener: ((bowlers: List<Bowler>) -> Unit)? = null) {
        appExecutor.ioThread {
            bowlerRepo.saveBowlers(bowlers)
            for(bowler in bowlers) {
                frameRepo.saveFrames(bowler.frames)
                rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
            }
            appExecutor.mainThread {
                listener?.invoke(bowlers)
            }
        }
    }

    fun saveResult(result: Result, listener: ((name: String) -> Unit)? = null) {
        appExecutor.ioThread {
            resultRepo.saveResult(result){ id, name ->
                for (bowler in result.bowlers) {
                    bowler.resultId = id
                    bowlerRepo.saveBowler(bowler)
                    frameRepo.saveFrames(bowler.frames)
                    rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
                }
                appExecutor.mainThread {
                    listener?.invoke(name)
                    resultRepo.getResults().observeForever {
                        var results = it
                    }
                }
            }
        }
    }

    fun getBowlers(bowlerIds: LongArray): LiveData<List<Bowler>> {
        return bowlerRepo.getBowlers(bowlerIds)
    }
}