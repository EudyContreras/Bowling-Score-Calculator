package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.room.*
import com.eudycontreras.bowlingcalculator.persistance.entities.FrameEntity

/**
 * Created by eudycontreras.
 */

@Dao
abstract class FramesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(frame: FrameEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(frame: List<FrameEntity>): List<Long>

    @Transaction
    open fun replaceAll(frames: List<FrameEntity>) {
        clear()
        insert(frames)
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(frame: FrameEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(frames: List<FrameEntity>): Int

    @Query(value = "SELECT * FROM frames WHERE `index` = :index AND bowlerId = :bowlerId LIMIT 1")
    abstract fun find(bowlerId: Long, index: Int): FrameEntity

    @Query(value = "SELECT * FROM frames WHERE bowlerId = :bowlerId ORDER BY `index` ASC")
    abstract fun findForBowler(bowlerId: Long): List<FrameEntity>

    @Query("SELECT * FROM frames WHERE bowlerId = -1")
    abstract fun getDefault(): List<FrameEntity>

    @Query("DELETE FROM frames WHERE bowlerId = :bowlerId")
    abstract fun delete(bowlerId: Long)

    @Query("DELETE FROM frames")
    abstract fun clear()

}