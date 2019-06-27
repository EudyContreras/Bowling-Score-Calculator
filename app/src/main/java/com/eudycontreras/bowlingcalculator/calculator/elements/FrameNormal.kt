package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_FRAME_CHANCES
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.utilities.NO_ID

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

data class FrameNormal(override var index: Int) : Frame() {

    companion object {
        const val FRAME_NORMAL: String = "FrameNormal"
    }

    override var bowlerId: Long = NO_ID

    override val type: String = FRAME_NORMAL

    override val rolls: LinkedHashMap<State, Roll> = LinkedHashMap()

    override var chances: Int = DEFAULT_FRAME_CHANCES

    override var pins: List<Pin> = List(DEFAULT_PIN_COUNT) { Pin() }

    override var state: State = State.FIRST_CHANCE

    override var bonusPoints: Int = 0

    override var pointsFromPrevious: Int = 0

    override val inProgress: Boolean
        get() = if (rolls.isNotEmpty()) {
            state != State.CLEARED
        } else false

    override val isCompleted: Boolean
        get() = !hasChances()

    override fun reset() {
        pointsFromPrevious = 0
        bonusPoints = 0
        state = State.FIRST_CHANCE
        chances = DEFAULT_FRAME_CHANCES
        resetPins()
    }

    override fun resetChances() {
        chances = DEFAULT_FRAME_CHANCES
    }

    override fun decreaseChances(value: Int) {
        chances = if (chances > 0) chances - value else 0

        updateState(chances)

        if (state == State.CLEARED) {
            resetPins()
        }
    }

    override fun updateState(chances: Int) {
        this.chances = chances
        this.state = when (chances) {
            2 -> State.FIRST_CHANCE
            1 -> State.SECOND_CHANCE
            else -> State.CLEARED
        }
    }

    override fun updateState(state: State) {
        this.state = state
        this.chances = when (state) {
            State.FIRST_CHANCE -> 2
            State.SECOND_CHANCE -> 1
            else -> 0
        }
    }

    override fun updateState(roll: Roll) {
        if (chances <= 0 || state == State.CLEARED) {
            reset()
            return
        }

        roll.frameIndex = index

        knockPins(roll.totalKnockdown)

        state = if (areAllPinsDown()) {
            val chance = assignState(chances)
            chances = 0
            chance
        } else {
            assignState(chances)
        }

        roll.parentState = state

        if (rolls.containsKey(state)) {
            handleEditedFrame(roll)
        }

        rolls[state] = roll

        decreaseChances(1)
    }

    override fun assignState(chances: Int): State {
       return when (chances) {
            2 -> State.FIRST_CHANCE
            1 -> State.SECOND_CHANCE
            else -> State.CLEARED
        }
    }

    override fun missingRounds(): Boolean {
        if(rolls.size < DEFAULT_FRAME_CHANCES) {
            if (!rolls.values.any { it.result == Roll.Result.STRIKE }) {
                return true
            }
        }
        return false
    }

    override fun handleEditedFrame(roll: Roll) {
        when (state) {
            State.FIRST_CHANCE -> {
                if (rolls.containsKey(State.SECOND_CHANCE)) {
                    val second = rolls.getValue(State.SECOND_CHANCE)
                    second.result = Roll.Result.from(roll, second.totalKnockdown)

                    if ((second.totalKnockdown + roll.totalKnockdown) > DEFAULT_PIN_COUNT) {
                        rolls.remove(State.SECOND_CHANCE)
                    } else {
                        rolls[State.SECOND_CHANCE] = second
                    }
                }
            }
            else -> { }
        }
    }
}