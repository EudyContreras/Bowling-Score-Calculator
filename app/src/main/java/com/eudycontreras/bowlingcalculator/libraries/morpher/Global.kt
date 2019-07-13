package com.eudycontreras.bowlingcalculator.libraries.morpher


fun interpolate(from: Int, to: Int, fraction: Float): Float {
    return from + (to - from) * fraction
}

fun intrpolate(from: Float, to: Float, fraction: Float): Float {
    return from + (to - from) * fraction
}

fun interpolate(from: Double, to: Double, fraction: Double): Double {
    return from + (to - from) * fraction
}