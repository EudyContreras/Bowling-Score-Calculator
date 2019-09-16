package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.persistance.entities.BowlerEntity

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

@Dao
abstract class BowlersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(bowler: BowlerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(bowlers: List<BowlerEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(bowler: BowlerEntity): Int

    @Transaction
    open suspend fun replace(bowler: BowlerEntity) {
        deleteById(bowler.id!!)
        insert(bowler)
    }

    @Query("SELECT count(*) FROM bowlers")
    abstract suspend fun getCount(): Int

    @Query("SELECT count(*) FROM bowlers WHERE resultId = :resultId")
    abstract suspend fun getCount(resultId: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM bowlers WHERE id = :bowlerId LIMIT 1)")
    abstract suspend fun exists(bowlerId: Long): Boolean

    @Query("SELECT * FROM bowlers WHERE id = :id LIMIT 1")
    abstract fun findById(id: Long): LiveData<BowlerEntity>

    @Query("SELECT * FROM bowlers WHERE id = :id LIMIT 1")
    abstract suspend fun getById(id: Long): BowlerEntity

    @Query("SELECT * FROM bowlers WHERE resultId = :id ORDER BY name ASC")
    abstract suspend fun findByResultId(id: Long): List<BowlerEntity>

    @Query("SELECT * FROM bowlers WHERE skill = :skill")
    abstract fun findBySkill(skill: Bowler.SkillLevel): LiveData<List<BowlerEntity>>

    @Query("SELECT * FROM bowlers WHERE id = -1 LIMIT 1")
    abstract fun getDefault(): LiveData<BowlerEntity>

    @Query("DELETE FROM bowlers WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("DELETE FROM bowlers WHERE resultId = :id")
    abstract suspend fun deleteByResultId(id: Long)

    @Query("DELETE FROM bowlers")
    abstract suspend fun clear()

}