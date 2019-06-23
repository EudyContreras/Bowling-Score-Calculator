package com.eudycontreras.bowlingcalculator.calculator.elements

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

data class Pin(var state: State = State.UP) : Element {

    enum class State { UP, DOWN, ALL }

    override fun init() {
        reset()
    }

    override fun reset() {
        state = State.UP
    }

    override fun toString(): String {
        return state.name
    }
}