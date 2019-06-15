package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eudycontreras.bowlingcalculator.persistance.entities.ResultEntity

/**
 * Created by eudycontreras.
 */

@Dao
abstract class ResultsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: ResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: List<ResultEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(result: ResultEntity): Int

    @Query(value = "SELECT * FROM results WHERE id = :id LIMIT 1")
    abstract fun findById(id: Long): LiveData<ResultEntity>

    @Query(value = "SELECT * FROM results ORDER BY name ASC")
    abstract fun findAllOrderByName(): LiveData<List<ResultEntity>>

    @Query(value = "SELECT * FROM results ORDER BY date DESC")
    abstract fun findAllOrderByDate(): LiveData<List<ResultEntity>>

    @Query("DELETE FROM results WHERE id = :id")
    abstract fun delete(id: Long)

    @Query("DELETE FROM results")
    abstract fun clear()

}