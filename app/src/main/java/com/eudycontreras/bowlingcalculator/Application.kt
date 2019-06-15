package com.eudycontreras.bowlingcalculator

import android.app.Application
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

    fun saveBowler(bowler: Bowler, toDb: Boolean) {
        storage.bowler = bowler
        if (toDb) {
            bowlerRepo.saveBowler(bowler)
            frameRepo.saveFrames(bowler.frames)
            rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
        }
    }

    fun saveResult(result: Result) {
        resultRepo.saveResult(result)
        for (bowler in result.bowlers) {
            bowlerRepo.saveBowler(bowler)
            frameRepo.saveFrames(bowler.frames)
            rollRepo.saveRolls(bowler.frames.flatMap { it.rolls.values })
        }
    }
}