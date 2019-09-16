package com.eudycontreras.bowlingcalculator.persistance.entities

import androidx.room.Entity
import androidx.room.Index
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameNormal
import com.eudycontreras.bowlingcalculator.calculator.elements.Pin

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

@Entity(
    tableName = "Frames",
    indices = [(Index("index", "bowlerId", unique = true))],
    primaryKeys = ["index", "bowlerId"]
)
data class FrameEntity(
    val bowlerId: Long,
    val type: String,
    val index: Int,
    val chances: Int,
    val pinsUp: Int,
    val state: Frame.State,
    val bonusPoints: Int,
    val pointsFromPrevious: Int
) {

    fun toFrame(): Frame {
        return when(type) {
            FrameNormal.FRAME_NORMAL -> FrameNormal(index).apply {
                bowlerId = this@FrameEntity.bowlerId
                index = this@FrameEntity.index
                chances = this@FrameEntity.chances
                pins = List(10) { i -> if(i<this@FrameEntity.pinsUp) Pin() else Pin(Pin.State.DOWN)}
                state = this@FrameEntity.state
                bonusPoints = this@FrameEntity.bonusPoints
                pointsFromPrevious = this@FrameEntity.pointsFromPrevious
            }
            else -> FrameLast(index).apply {
                bowlerId = this@FrameEntity.bowlerId
                index = this@FrameEntity.index
                chances = this@FrameEntity.chances
                pins = List(10) { i -> if(i<this@FrameEntity.pinsUp) Pin() else Pin(Pin.State.DOWN)}
                state = this@FrameEntity.state
                bonusPoints = this@FrameEntity.bonusPoints
                pointsFromPrevious = this@FrameEntity.pointsFromPrevious
            }
        }
    }

    companion object {
        fun from(frame: Frame): FrameEntity {
            return FrameEntity(
                type = frame.type,
                index = frame.index,
                bowlerId = frame.bowlerId,
                chances = frame.chances,
                pinsUp = frame.pinUpCount(),
                state = frame.state,
                bonusPoints = frame.bonusPoints,
                pointsFromPrevious = frame.pointsFromPrevious
            )
        }
    }
}