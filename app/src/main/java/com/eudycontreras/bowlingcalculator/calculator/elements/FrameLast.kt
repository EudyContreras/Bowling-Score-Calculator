package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_FRAME_CHANCES
import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_PIN_COUNT
import com.eudycontreras.bowlingcalculator.utilities.NO_ID

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

data class FrameLast(override var index: Int) : Frame() {

    companion object {
        const val FRAME_LAST: String = "FrameLast"
    }

    override var bowlerId: Long = NO_ID

    override val type: String = FRAME_LAST

    override val rolls: LinkedHashMap<State, Roll> = LinkedHashMap()

    override var chances: Int = DEFAULT_FRAME_CHANCES + 1

    override var pins: List<Pin> = List(DEFAULT_PIN_COUNT) { Pin() }

    override var state: State = State.FIRST_CHANCE

    override var bonusPoints: Int = 0

    override var pointsFromPrevious: Int = 0

    override fun reset() {
        pointsFromPrevious = 0
        bonusPoints = 0
        state = State.FIRST_CHANCE
        chances = DEFAULT_FRAME_CHANCES + 1
        resetPins()
    }

    override fun resetChances() {
        chances = DEFAULT_FRAME_CHANCES + 1
    }

    override fun decreaseChances(value: Int) {
        chances = if (chances > 0) chances - value else 0

        updateState(chances)

        if (state == State.CLEARED) {
            resetPins()
        }

        if (state == State.EXTRA_CHANCE) {
            if (rolls.getValue(State.FIRST_CHANCE).result != Roll.Result.STRIKE) {
                if (rolls.getValue(State.SECOND_CHANCE).result != Roll.Result.SPARE) {
                    resetPins()
                    chances = 0
                    state = State.CLEARED
                    rolls.remove(State.EXTRA_CHANCE)
                }
            }
        }
    }

    override fun updateState(chances: Int) {
        this.chances = chances
        this.state = when (chances) {
            3 -> State.FIRST_CHANCE
            2 -> State.SECOND_CHANCE
            1 -> State.EXTRA_CHANCE
            else -> State.CLEARED
        }
    }

    override fun updateState(state: State) {
        this.state = state
        this.chances = when (state) {
            State.FIRST_CHANCE -> 3
            State.SECOND_CHANCE -> 2
            State.EXTRA_CHANCE -> 1
            State.CLEARED -> 0
        }
    }

    override fun updateState(roll: Roll) {
        if (chances <= 0 || state == State.CLEARED) {
            return
        }

        roll.frameIndex = index

        knockPins(roll.totalKnockdown)

        state = if (areAllPinsDown()) {
            resetPins()
            assignState(chances)
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
        return  when (chances) {
            3 -> State.FIRST_CHANCE
            2 -> State.SECOND_CHANCE
            1 -> State.EXTRA_CHANCE
            else -> State.CLEARED
        }
    }

    override fun missingRounds(): Boolean {
        if(rolls.size < DEFAULT_FRAME_CHANCES + 1) {
            return true
        }
        return false
    }


    override fun handleEditedFrame(roll: Roll) {
        when (state) {
            State.FIRST_CHANCE -> {
                if (roll.result != Roll.Result.STRIKE) {
                    val second = rolls.getValue(State.SECOND_CHANCE)
                    second.result = Roll.Result.from(roll, second.totalKnockdown)
                    if (second.result != Roll.Result.SPARE) {
                        rolls.remove(State.EXTRA_CHANCE)
                    }
                    if ((second.totalKnockdown + roll.totalKnockdown) > DEFAULT_PIN_COUNT) {
                        rolls.remove(State.SECOND_CHANCE)
                        rolls.remove(State.EXTRA_CHANCE)
                    } else {
                        rolls[State.SECOND_CHANCE] = second
                    }
                } else {
                    rolls.remove(State.SECOND_CHANCE)
                    rolls.remove(State.EXTRA_CHANCE)
                }
            }
            State.SECOND_CHANCE -> {
                val first = rolls.getValue(State.FIRST_CHANCE)
                if (first.result == Roll.Result.STRIKE){
                    val extra = getRollBy(State.EXTRA_CHANCE)
                    if (extra != null) {
                        extra.result = Roll.Result.from(roll, extra.totalKnockdown)

                        if ((extra.totalKnockdown + roll.totalKnockdown) > DEFAULT_PIN_COUNT) {
                            rolls.remove(State.EXTRA_CHANCE)
                        } else {
                            rolls[State.EXTRA_CHANCE] = extra
                        }
                    }
                }
                if (roll.result == Roll.Result.SPARE) {
                    rolls.remove(State.EXTRA_CHANCE)
                }
            }
            else -> { }
        }
    }
}