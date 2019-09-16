package com.eudycontreras.bowlingcalculator.utilities

import androidx.core.math.MathUtils.clamp
import com.eudycontreras.bowlingcalculator.adapters.FrameTypeAdapter
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
val gson: Gson by lazy {
    GsonBuilder()
        .registerTypeAdapter(Frame::class.java, FrameTypeAdapter())
        .create()
}

fun <T> toString(element: T): String {
    return element.toString()
}

fun mapRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float, clampMin: Float, clampMax: Float): Float {
    return clamp(((value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin), clampMin, clampMax)
}

fun clamp(value: Float, minValue: Float, maxValue: Float): Float {
    return if (value < minValue) minValue else if (value > maxValue) maxValue else value
}

inline fun <reified X,reified Y> doWith(first: X?, second: Y?, capsule: (X,Y) -> Unit) {
    if (first != null && second != null) {
        return capsule.invoke(first, second)
    }
}

inline fun <reified X,reified Y,reified Z> doWith(first: X?, second: Y?, third: Z?, capsule: (X,Y,Z) -> Unit) {
    if (first != null && second != null && third != null) {
        return capsule.invoke(first, second, third)
    }
    throw NullParameterException("")
}

fun runAfterMain(delay: Long, action: ()-> Unit) {
    GlobalScope.launch(Dispatchers.Main.immediate) {
        delay(delay)
        action.invoke()
    }
}

fun runSequential(delay: Long, times: Int, applyAction: (index: Int) -> Unit) {
    if (times == 0)
        return

    GlobalScope.launch(Dispatchers.Main.immediate) {
        for (counter in 0 until times) {
            delay(delay)
            applyAction(counter)
        }
    }
}

fun runSequential(delay: Long, times: Int, onEnd: (() -> Unit)? = null, applyAction: (index: Int) -> Unit) {
    if (times == 0)
        return

    GlobalScope.launch(Dispatchers.Main.immediate) {
        for (counter in 0 until times) {
            delay(delay)
            applyAction(counter)
        }
        delay(delay * 2)
        onEnd?.invoke()
    }
}