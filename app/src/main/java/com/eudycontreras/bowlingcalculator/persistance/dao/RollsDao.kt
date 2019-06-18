package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.room.*
import com.eudycontreras.bowlingcalculator.persistance.entities.RollEntity

/**
 * Created by eudycontreras.
 */

@Dao
abstract class RollsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(roll: RollEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(roll: List<RollEntity>): List<Long>

    @Transaction
    open fun replaceAll(rolls: List<RollEntity>) {
        clear()
        insert(rolls)
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(roll: RollEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(roll: List<RollEntity>): Int

    @Query(value = "SELECT * FROM rolls WHERE bowlerId = :id")
    abstract fun findByBowlerId(id: Long): List<RollEntity>

    @Query(value = "SELECT * FROM rolls WHERE bowlerId = :bowlerId AND frameIndex = :frameIndex")
    abstract fun findByBowlerIdAndIndex(bowlerId: Long, frameIndex: Int): List<RollEntity>

    @Query(value = "SELECT * FROM rolls WHERE bowlerId = :bowlerId AND frameIndex = :frameIndex")
    abstract fun getByBowlerIdAndIndex(bowlerId: Long, frameIndex: Int): List<RollEntity>

    @Query("DELETE FROM rolls WHERE frameIndex = :frameIndex AND bowlerId = :bowlerId")
    abstract fun delete(frameIndex: Int, bowlerId: Long)

    @Query("DELETE FROM rolls WHERE bowlerId = :bowlerId")
    abstract fun delete(bowlerId: Long)

    @Query("DELETE FROM rolls")
    abstract fun clear()

}