package com.eudycontreras.bowlingcalculator.libraries.morpher

import androidx.core.math.MathUtils


fun interpolate(from: Int, to: Int, fraction: Float): Float {
    return from + (to - from) * fraction
}

fun intrpolate(from: Float, to: Float, fraction: Float): Float {
    return from + (to - from) * fraction
}

fun interpolate(from: Double, to: Double, fraction: Double): Double {
    return from + (to - from) * fraction
}

fun mapRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun mapRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float, clampMin: Float, clampMax: Float): Float {
    return MathUtils.clamp(((value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin), clampMin, clampMax)
}
