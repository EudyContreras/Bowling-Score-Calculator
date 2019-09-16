package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.room.*
import com.eudycontreras.bowlingcalculator.persistance.entities.FrameEntity

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

@Dao
abstract class FramesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(frame: FrameEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(frame: List<FrameEntity>): List<Long>

    @Transaction
    open suspend fun  replaceAll(frames: List<FrameEntity>) {
        clear()
        insert(frames)
    }

    @Transaction
    open suspend fun replaceFor(bowlerId: Long, frames: List<FrameEntity>) {
        delete(bowlerId)
        insert(frames)
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(frame: FrameEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(frames: List<FrameEntity>): Int

    @Query("SELECT * FROM frames WHERE `index` = :index AND bowlerId = :bowlerId LIMIT 1")
    abstract suspend fun find(bowlerId: Long, index: Int): FrameEntity

    @Query("SELECT * FROM frames WHERE bowlerId = :bowlerId ORDER BY `index` ASC")
    abstract suspend fun findForBowler(bowlerId: Long): List<FrameEntity>

    @Query("SELECT * FROM frames WHERE bowlerId = -1")
    abstract suspend fun getDefault(): List<FrameEntity>

    @Query("DELETE FROM frames WHERE bowlerId = :bowlerId")
    abstract suspend fun delete(bowlerId: Long)

    @Query("DELETE FROM frames")
    abstract suspend fun clear()

}