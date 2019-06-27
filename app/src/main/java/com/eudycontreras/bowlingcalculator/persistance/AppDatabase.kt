package com.eudycontreras.bowlingcalculator.persistance


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eudycontreras.bowlingcalculator.persistance.dao.BowlersDao
import com.eudycontreras.bowlingcalculator.persistance.dao.FramesDao
import com.eudycontreras.bowlingcalculator.persistance.dao.ResultsDao
import com.eudycontreras.bowlingcalculator.persistance.dao.RollsDao
import com.eudycontreras.bowlingcalculator.persistance.entities.BowlerEntity
import com.eudycontreras.bowlingcalculator.persistance.entities.FrameEntity
import com.eudycontreras.bowlingcalculator.persistance.entities.ResultEntity
import com.eudycontreras.bowlingcalculator.persistance.entities.RollEntity
import com.eudycontreras.bowlingcalculator.utilities.DB_VERSION

/**
 * App database, defining the different DAOs
 *
 * Exports the schema of each version to $projectDir/schemas/
 * The path is defined in app/build.gradle
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
*/
private const val DATABASE_NAME = "bowling_calculator.db"

@Database(
    entities = [
        BowlerEntity::class,
        FrameEntity::class,
        RollEntity::class,
        ResultEntity::class
    ],
    version = DB_VERSION,
    exportSchema = true
)

@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getBowlers(): BowlersDao
    val bowler
        get() = getBowlers()

    abstract fun getFrames(): FramesDao
    val frame
        get() = getFrames()

    abstract fun getRolls(): RollsDao
    val roll
        get() = getRolls()

    abstract fun getResults(): ResultsDao
    val result
        get() = getResults()

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return instance!!
        }
    }
}