package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.room.*
import com.eudycontreras.bowlingcalculator.persistance.entities.MappingEntity

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

@Dao
abstract class MappingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(mappers: MappingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(mappings: List<MappingEntity>)

    @Transaction
    open suspend fun replaceAll(mappings: List<MappingEntity>) {
        clear()
        insert(mappings)
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(mapping: MappingEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(mapping: List<MappingEntity>): Int

    @Query("SELECT * FROM mappings WHERE resultId = :resultId")
    abstract suspend fun getForResult(resultId: Long): List<MappingEntity>

    @Query("DELETE FROM mappings WHERE resultId = :resultId")
    abstract suspend fun deleteForResult(resultId: Long)

    @Query("DELETE FROM mappings WHERE resultId = :resultId AND bowlerId = :bowlerId")
    abstract suspend fun delete(resultId: Long, bowlerId: Long)

    @Query("DELETE FROM mappings")
    abstract suspend fun clear()

}