package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.DEFAULT_FRAME_CHANCES

/**
 * Created by eudycontreras.
 */

abstract class Frame : Element {

    enum class State {
        FIRST_CHANCE,
        SECOND_CHANCE,
        EXTRA_CHANCE,
        CLEARED
    }

    abstract var index: Int

    abstract val type: String

    abstract val rolls: LinkedHashMap<Frame.State, Roll>

    abstract var chances: Int

    abstract val pins: List<Pin>

    abstract var state: State

    abstract var bonusPoints: Int

    abstract var pointsFromPrevious: Int

    abstract val inProgress: Boolean

    abstract val isCompleted: Boolean

    fun hasChances(): Boolean = chances > 0

    fun getRollBy(chance: Frame.State): Roll? = rolls[chance]

    fun areAllPinsDown(): Boolean = pins.all { it.state == Pin.State.DOWN }

    fun pinUpCount(): Int= pins.count { it.state == Pin.State.UP }

    abstract fun getTotal(includeAccumulated: Boolean): Int

    abstract fun resetChances()

    abstract fun decreaseChances(value: Int = 1)

    abstract fun updateState(chances: Int)

    abstract fun updateState(state: State)

    abstract fun updateState(roll: Roll)

    abstract fun assignState(chances: Int = DEFAULT_FRAME_CHANCES): State

    abstract fun missingRounds(): Boolean

    abstract fun handleEditedFrame(roll: Roll)

    protected fun knockPins(count: Int) {
        pins.filter { it.state == Pin.State.UP }
            .take(count)
            .forEach { it.state = Pin.State.DOWN }
    }

    fun resetPins() {
        pins.forEach { it.state = Pin.State.UP }
    }
}