package com.eudycontreras.bowlingcalculator.utilities.extensions

import kotlinx.coroutines.Job

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
fun Job.onEnd(onEnd: (()-> Unit)?) {
    onEnd?.let { task ->
        invokeOnCompletion {
            task()
        }
    }
}

fun <T> Job.onEnd(value: T, onEnd: ((T)-> Unit)?) {
    onEnd?.let { task ->
        invokeOnCompletion {
            task(value)
        }
    }
}

inline fun <reified T> Job.onFinished(value: T, crossinline onEnd: ((T)-> Unit)) {
    invokeOnCompletion {
        onEnd.invoke(value)
    }
}
