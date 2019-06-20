package com.eudycontreras.bowlingcalculator

import kotlinx.coroutines.*


/**
 * Created by eudycontreras.
 */

fun <T> toString(element: T): String {
    return element.toString()
}

fun fromMain(task: (()-> Unit)?): Job {
    return GlobalScope.launch(Dispatchers.Main.immediate) {
        task?.invoke()
    }
}

fun <T> fromMain(result: T, task: ((T)-> Unit)?): Job {
    return GlobalScope.launch(Dispatchers.Main.immediate) {
        task?.invoke(result)
    }
}

fun fromScopeMain(task: ((CoroutineScope)-> Unit)?): Job {
    return GlobalScope.launch(Dispatchers.Main.immediate) {
        task?.invoke(this)
    }
}

fun fromIO(task: (()-> Unit)?): Job {
    return GlobalScope.launch(Dispatchers.IO) {
        task?.invoke()
    }
}

fun fromScopeIO(task: ((CoroutineScope)-> Unit)?): Job {
    return GlobalScope.launch(Dispatchers.IO) {
        task?.invoke(this)
    }
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

    GlobalScope.launch {
        for (counter in 0 until times) {
            applyAction(counter)
            delay(delay)
        }
    }
}