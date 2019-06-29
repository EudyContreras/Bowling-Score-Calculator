package com.eudycontreras.bowlingcalculator.calculator.elements

import com.eudycontreras.bowlingcalculator.utilities.DEFAULT_FRAME_CHANCES

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

abstract class Frame : Element {

    enum class State {
        FIRST_CHANCE,
        SECOND_CHANCE,
        EXTRA_CHANCE,
        CLEARED;

        override fun toString(): String{
            return name
        }
    }

    companion object {
        const val TYPE_KEY: String = "type"
    }

    abstract var bowlerId: Long

    abstract var index: Int

    abstract val type: String

    abstract val rolls: LinkedHashMap<State, Roll>

    abstract var chances: Int

    abstract val pins: List<Pin>

    abstract var state: State

    abstract var bonusPoints: Int

    abstract var pointsFromPrevious: Int

    val inProgress: Boolean
        get() = if (rolls.isNotEmpty()) {
            state != State.CLEARED
        } else false

    val isCompleted: Boolean
        get() = !hasChances()

    fun hasChances(): Boolean = chances > 0

    fun hasStarted(): Boolean = rolls.size > 0

    fun getRollBy(chance: State): Roll? = rolls[chance]

    fun areAllPinsDown(): Boolean = pins.all { it.state == Pin.State.DOWN }

    fun pinUpCount(): Int= pins.count { it.state == Pin.State.UP }

    fun getTotal(includeAccumulated: Boolean = false, includeBonus: Boolean = true): Int {
        var sum = rolls.values.map { it.totalKnockdown }.sum()
        sum += if (includeAccumulated) pointsFromPrevious else 0
        sum += if (includeBonus) bonusPoints else 0
        return sum
    }

    protected fun knockPins(count: Int) {
        pins.filter { it.state == Pin.State.UP }
            .take(count)
            .forEach { it.state = Pin.State.DOWN }
    }

    fun resetPins() {
        pins.forEach { it.state = Pin.State.UP }
    }

    override fun init() {
        reset()
    }

    abstract fun resetChances()

    abstract fun decreaseChances(value: Int = 1)

    abstract fun updateState(chances: Int)

    abstract fun updateState(state: State)

    abstract fun updateState(roll: Roll)

    abstract fun assignState(chances: Int = DEFAULT_FRAME_CHANCES): State

    abstract fun missingRounds(): Boolean

    abstract fun handleEditedFrame(roll: Roll)

    override fun toString(): String {
        return "Frame(index=$index, type='$type', chances=$chances, state=$state)"
    }
}