package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.calculator.ScoreCalculator
import com.eudycontreras.bowlingcalculator.calculator.listeners.ScoreStateListener
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_FRAME_COUNT
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.utilities.NO_ID
import com.eudycontreras.bowlingcalculator.utilities.extensions.clamp
import com.eudycontreras.bowlingcalculator.utilities.extensions.doWhen

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

data class Bowler(
    var name: String = "",
    var skill: SkillLevel = SkillLevel.AMATEUR
) : Element {

    enum class SkillLevel{
        NOVICE,
        AMATEUR,
        EXPERIENCED,
        PROFESSIONAL
    }

    var id: Long = NO_ID

    var resultId: Long = NO_ID

    var frames: List<Frame> = List(DEFAULT_FRAME_COUNT) { i ->
        if (i < DEFAULT_FRAME_COUNT - 1) {
            FrameNormal(i)
        } else {
            FrameLast(i)
        }
    }

    var lastPlayedFrameIndex: Int = DEFAULT_START_INDEX

    var currentFrameIndex: Int = DEFAULT_START_INDEX
        set(value) {
            if (value > lastPlayedFrameIndex) {
                lastPlayedFrameIndex = frames[value].index
            }
            field = value
        }

    private fun moveToNextFrame() {
        currentFrameIndex = (currentFrameIndex + 1)
            .clamp(DEFAULT_START_INDEX, DEFAULT_FRAME_COUNT - 1)
    }

    fun hasStarted(): Boolean {
        return currentFrameIndex > 0 || getCurrentFrame().hasStarted()
    }

    fun getNextFrame() = frames[currentFrameIndex + 1]

    fun getCurrentFrame() = frames[currentFrameIndex]

    override fun init() {
        frames.forEach { it.init() }
        currentFrameIndex = DEFAULT_START_INDEX
    }

    override fun reset() {
        lastPlayedFrameIndex = DEFAULT_START_INDEX
        currentFrameIndex = DEFAULT_START_INDEX
        frames.forEach {
            it.reset()
            it.rolls.clear()
        }
    }

    fun performRoll(pinCount: Int, listener: ScoreStateListener? = null) {
        performRoll(pinCount, listener, false)
    }

    fun performRoll(pinCount: Int, listener: ScoreStateListener? = null, fromSimulation: Boolean = false) {

        val roll = Roll(bowlerId = this.id, totalKnockdown = pinCount)

        var currentFrame = getCurrentFrame()

        currentFrame.doWhen( { !hasChances() and (this is FrameLast) and !isCompleted } ) {
            it.reset()
        }

        when (currentFrame) {
            is FrameNormal -> handleNormalFrameThrow(pinCount, roll, currentFrame)
            else -> handleLastFrameThrow(pinCount, roll, currentFrame)
        }

        currentFrame.updateState(roll)

        if (!currentFrame.hasChances()) {
            val lastFrame = getCurrentFrame()

            moveToNextFrame()
            if (!fromSimulation) {
                currentFrame = getCurrentFrame()
                if (currentFrame is FrameLast) {
                    if (!currentFrame.isCompleted) {
                        currentFrame.reset()
                    } else {
                        if (lastFrame !is FrameLast) {
                            currentFrame.state = Frame.State.FIRST_CHANCE
                            currentFrame.resetChances()
                            currentFrame.resetPins()
                        }
                    }
                } else {
                    currentFrame.reset()
                }
            }
        }

        ScoreCalculator.calculateScore(this, listener, fromSimulation)
    }

    private fun handleNormalFrameThrow(pinCount: Int, roll: Roll, frame: Frame) {
        roll.result = if (frame.state == Frame.State.FIRST_CHANCE) {
            Roll.Result.from(null, pinCount)
        } else {
            val lastRoll = frame.getRollBy(Frame.State.FIRST_CHANCE)
            Roll.Result.from(lastRoll, pinCount)
        }
    }

    private fun handleLastFrameThrow(pinCount: Int, roll: Roll, frame: Frame) {
        roll.result = when (frame.state) {
            Frame.State.FIRST_CHANCE -> {
                Roll.Result.from(null, pinCount)
            }
            Frame.State.SECOND_CHANCE -> {
                val lastRoll = frame.getRollBy(Frame.State.FIRST_CHANCE)
                Roll.Result.from(lastRoll, pinCount)
            }
            Frame.State.EXTRA_CHANCE -> {
                val lastRoll = frame.getRollBy(Frame.State.SECOND_CHANCE)

                if (lastRoll != null) {
                    if (lastRoll.result == Roll.Result.SPARE || lastRoll.result == Roll.Result.STRIKE) {
                        Roll.Result.from(null, pinCount)
                    } else {
                        Roll.Result.from(lastRoll, pinCount)
                    }
                } else {
                    Roll.Result.from(lastRoll, pinCount)
                }
            }
            else -> Roll.Result.UNKNOWN
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Bowler) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Bowler(name='$name', id=$id)"
    }
}