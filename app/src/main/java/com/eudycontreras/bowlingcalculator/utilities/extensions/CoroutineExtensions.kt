package com.eudycontreras.bowlingcalculator.utilities.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

suspend fun CoroutineScope.fromMain(action: (()-> Unit)?) {
    action?.let { task ->
        withContext(Dispatchers.Main) {
            task.invoke()
        }
    }
}

suspend fun CoroutineScope.fromIO(action: (()-> Unit)?) {
    action?.let { task ->
        withContext(Dispatchers.IO) {
            task.invoke()
        }
    }
}

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
