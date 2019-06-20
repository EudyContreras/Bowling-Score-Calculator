package com.eudycontreras.bowlingcalculator.persistance.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.persistance.entities.BowlerEntity

/**
 * Created by eudycontreras.
 */

@Dao
abstract class BowlersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(bowler: BowlerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(bowlers: List<BowlerEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(bowler: BowlerEntity): Int

    @Transaction
    open fun replace(bowler: BowlerEntity) {
        deleteById(bowler.id!!)
        insert(bowler)
    }

    @Query("SELECT count(*) FROM bowlers")
    abstract fun getCount(): Int

    @Query("SELECT count(*) FROM bowlers WHERE resultId = :resultId")
    abstract fun getCount(resultId: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM bowlers WHERE id = :bowlerId LIMIT 1)")
    abstract fun exists(bowlerId: Long): Boolean

    @Query("SELECT * FROM bowlers WHERE id = :id LIMIT 1")
    abstract fun findById(id: Long): LiveData<BowlerEntity>

    @Query("SELECT * FROM bowlers WHERE id = :id LIMIT 1")
    abstract fun getById(id: Long): BowlerEntity

    @Query("SELECT * FROM bowlers WHERE resultId = :id ORDER BY name ASC")
    abstract fun findByResultId(id: Long): List<BowlerEntity>

    @Query("SELECT * FROM bowlers WHERE skill = :skill")
    abstract fun findBySkill(skill: Bowler.SkillLevel): LiveData<List<BowlerEntity>>

    @Query("SELECT * FROM bowlers WHERE id = -1 LIMIT 1")
    abstract fun getDefault(): LiveData<BowlerEntity>

    @Query("DELETE FROM bowlers WHERE id = :id")
    abstract fun deleteById(id: Long)

    @Query("DELETE FROM bowlers WHERE resultId = :id")
    abstract fun deleteByResultId(id: Long)

    @Query("DELETE FROM bowlers")
    abstract fun clear()

}