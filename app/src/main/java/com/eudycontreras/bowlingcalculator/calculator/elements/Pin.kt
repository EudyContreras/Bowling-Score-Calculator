package com.eudycontreras.bowlingcalculator.calculator.elements

/**
 * Created by eudycontreras.
 */


data class Pin(var id: Int = 0) : Element {

    enum class State { UP, DOWN, ALL }

    var state: State = State.UP

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