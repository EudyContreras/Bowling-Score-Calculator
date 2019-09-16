package com.eudycontreras.bowlingcalculator.calculator.elements

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

data class Pin(var state: State = State.UP) : Element {

    enum class State { UP, DOWN }

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