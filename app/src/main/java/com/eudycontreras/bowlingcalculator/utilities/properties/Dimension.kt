package com.eudycontreras.bowlingcalculator.utilities.properties

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
data class Dimension(
    var width: Float = 0f,
    var height: Float = 0f
) {
    operator fun plus(other: Dimension): Dimension {
        val dimension = Dimension(this.width, this.height)
        dimension.width += other.width
        dimension.height += other.height
        return dimension
    }

    operator fun plus(other: Int): Dimension {
        val dimension = Dimension(this.width, this.height)
        dimension.width += other.toFloat()
        dimension.height += other.toFloat()
        return dimension
    }

    operator fun plusAssign(other: Dimension) {
        this.width += other.width
        this.height += other.height
    }

    operator fun plusAssign(other: Int) {
        this.width += other.toFloat()
        this.height += other.toFloat()
    }

    operator fun minus(other: Dimension): Dimension {
        val dimension = Dimension(this.width, this.height)
        dimension.width -= other.width
        dimension.height -= other.height
        return dimension
    }

    operator fun minus(other: Int): Dimension {
        val dimension = Dimension(this.width, this.height)
        dimension.width -= other.toFloat()
        dimension.height -= other.toFloat()
        return dimension
    }

    operator fun minusAssign(other: Dimension) {
        this.width -= other.width
        this.height -= other.height
    }

    operator fun minusAssign(other: Int) {
        this.width -= other.toFloat()
        this.height -= other.toFloat()
    }

    fun copyProps(): Dimension {
        return Dimension(width, height)
    }
}

