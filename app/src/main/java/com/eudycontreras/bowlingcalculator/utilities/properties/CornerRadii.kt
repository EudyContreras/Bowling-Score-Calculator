package com.eudycontreras.bowlingcalculator.utilities.properties

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
class CornerRadii {
    var rx: Float = 0f
    var ry: Float = 0f

    var topLeft: Boolean = false
    var topRight: Boolean = false
    var bottomLeft: Boolean = false
    var bottomRight: Boolean = false

    val values: FloatArray by lazy {
        val tl = if (topLeft) Pair(rx, ry) else Pair(0f, 0f)
        val tr = if (topRight) Pair(rx, ry) else Pair(0f, 0f)
        val bl = if (bottomLeft) Pair(rx, ry) else Pair(0f, 0f)
        val br = if (bottomRight) Pair(rx, ry) else Pair(0f, 0f)

        return@lazy floatArrayOf(
            tl.first, tl.second,
            tr.first, tr.second,
            bl.first, bl.second,
            br.first, br.second
        )
    }

    fun reset() {
        rx = 0f
        ry = 0f

        topLeft = false
        topRight = false
        bottomLeft = false
        bottomRight = false
    }

    fun copyProps(cornerRadii: CornerRadii) {
        this.rx = cornerRadii.rx
        this.ry = cornerRadii.ry

        this.topLeft = cornerRadii.topLeft
        this.topRight = cornerRadii.topRight
        this.bottomLeft = cornerRadii.bottomLeft
        this.bottomRight = cornerRadii.bottomRight
    }
}