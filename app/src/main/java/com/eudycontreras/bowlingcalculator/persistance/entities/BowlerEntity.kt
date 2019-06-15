package com.eudycontreras.bowlingcalculator.persistance.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eudycontreras.bowlingcalculator.NO_ID
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler

/**
 * Created by eudycontreras.
 */

@Entity(tableName = "Bowlers")
data class BowlerEntity(
    val name: String,
    val resultId: Long?,
    val skill: Bowler.SkillLevel,
    val lastPlayedFrameIndex: Int,
    val currentFrameIndex: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = NO_ID

    fun toBowler(): Bowler {
        return Bowler(name, skill).apply {
            lastPlayedFrameIndex = this@BowlerEntity.lastPlayedFrameIndex
            currentFrameIndex = this@BowlerEntity.currentFrameIndex
            resultId = this@BowlerEntity.resultId
            id = this@BowlerEntity.id
        }
    }

    companion object {
        fun from(bowler: Bowler): BowlerEntity {
            return BowlerEntity(
                name = bowler.name,
                skill = bowler.skill,
                resultId = bowler.resultId,
                lastPlayedFrameIndex = bowler.lastPlayedFrameIndex,
                currentFrameIndex = bowler.currentFrameIndex
            ).also { it.id = bowler.id }
        }
    }
}