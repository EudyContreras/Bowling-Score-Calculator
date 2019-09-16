package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.room.*
import com.eudycontreras.bowlingcalculator.persistance.entities.RollEntity

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

@Dao
abstract class RollsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(roll: RollEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(roll: List<RollEntity>): List<Long>

    @Transaction
    open suspend fun replaceAll(rolls: List<RollEntity>) {
        clear()
        insert(rolls)
    }

    @Transaction
    open suspend fun replaceAllFor(bowlerId: Long, rolls: List<RollEntity>) {
        delete(bowlerId)
        insert(rolls)
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(roll: RollEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(roll: List<RollEntity>): Int

    @Query("SELECT * FROM rolls WHERE bowlerId = :id")
    abstract suspend fun findByBowlerId(id: Long): List<RollEntity>

    @Query("SELECT * FROM rolls WHERE bowlerId = :bowlerId AND frameIndex = :frameIndex")
    abstract suspend fun findByBowlerIdAndIndex(bowlerId: Long, frameIndex: Int): List<RollEntity>

    @Query("SELECT * FROM rolls WHERE bowlerId = :bowlerId AND frameIndex = :frameIndex")
    abstract suspend fun getByBowlerIdAndIndex(bowlerId: Long, frameIndex: Int): List<RollEntity>

    @Query("DELETE FROM rolls WHERE bowlerId = :bowlerId AND frameIndex = :frameIndex")
    abstract suspend fun delete(bowlerId: Long, frameIndex: Int)

    @Query("DELETE FROM rolls WHERE bowlerId = :bowlerId")
    abstract suspend fun delete(bowlerId: Long)

    @Query("DELETE FROM rolls")
    abstract suspend fun clear()

}