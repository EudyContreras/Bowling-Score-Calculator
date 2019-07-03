package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eudycontreras.bowlingcalculator.persistance.entities.ResultEntity

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

@Dao
abstract class ResultsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(result: ResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(result: List<ResultEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(result: ResultEntity): Int

    @Query("SELECT count(*) FROM results")
    abstract suspend fun getCount(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM results WHERE id = :resultId  LIMIT 1)")
    abstract suspend fun exists(resultId: Long): Boolean

    @Query(value = "SELECT * FROM results WHERE id = :id LIMIT 1")
    abstract fun findById(id: Long): LiveData<ResultEntity>

    @Query(value = "SELECT * FROM results ORDER BY name ASC")
    abstract fun findAllOrderByName(): LiveData<List<ResultEntity>>

    @Query(value = "SELECT * FROM results ORDER BY date DESC")
    abstract fun findAllOrderByDate(): LiveData<List<ResultEntity>>

    @Query("DELETE FROM results WHERE id = :id")
    abstract suspend fun delete(id: Long)

    @Query("DELETE FROM results")
    abstract suspend fun clear()

}