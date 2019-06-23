package com.eudycontreras.bowlingcalculator.persistance.entities

import androidx.room.Entity
import androidx.room.Index
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

@Entity(
    tableName = "Rolls",
    indices = [(Index( "bowlerId", "frameIndex", "parentState"))],
    primaryKeys = ["bowlerId", "frameIndex", "parentState"]
)
data class RollEntity(
    val bowlerId: Long,
    val frameIndex: Int,
    val parentState: Frame.State,
    val totalKnockdown: Int,
    val result: Roll.Result
) {
    fun toRoll(): Roll {
        return Roll(bowlerId, totalKnockdown).apply {
            result = this@RollEntity.result
            frameIndex = this@RollEntity.frameIndex
            parentState = this@RollEntity.parentState
        }
    }

    companion object {

        fun from(roll: Roll): RollEntity {
            return RollEntity(
                bowlerId = roll.bowlerId,
                frameIndex = roll.frameIndex,
                parentState = roll.parentState,
                totalKnockdown = roll.totalKnockdown,
                result = roll.result
            )
        }
    }
}