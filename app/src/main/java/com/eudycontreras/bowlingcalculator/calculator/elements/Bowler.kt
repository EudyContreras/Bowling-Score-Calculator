package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.calculator.ScoreCalculator
import com.eudycontreras.bowlingcalculator.calculator.listeners.ScoreStateListener
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_FRAME_COUNT
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_START_INDEX
import com.eudycontreras.bowlingcalculator.utilities.NO_ID
import com.eudycontreras.bowlingcalculator.utilities.extensions.clamp

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

data class Bowler(
    val name: String = "",
    val skill: SkillLevel = SkillLevel.AMATEUR
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

        if (!currentFrame.hasChances()) {
            if (currentFrame is FrameLast) {
                if (!currentFrame.isCompleted) {
                    currentFrame.reset()
                }
            }
        }

        if (currentFrame is FrameNormal) {
            handleNormalFrameThrow(pinCount, roll, currentFrame)
        } else {
            handleLastFrameThrow(pinCount, roll, currentFrame)
        }

        currentFrame.updateState(roll)

        if (!currentFrame.hasChances()) {
            moveToNextFrame()
            currentFrame = getCurrentFrame()
            if (currentFrame is FrameLast) {
                if (!currentFrame.isCompleted) {
                    currentFrame.reset()
                }
            } else {
                currentFrame.reset()
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
                Roll.Result.from(lastRoll, pinCount)
            }
            else -> Roll.Result.UNKNOWN
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bowler) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}