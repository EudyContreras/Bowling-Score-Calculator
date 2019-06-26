package com.eudycontreras.bowlingcalculator.utilities

import com.eudycontreras.bowlingcalculator.adapters.FrameTypeAdapter
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*


/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

val gson: Gson by lazy {
    GsonBuilder()
        .registerTypeAdapter(Frame::class.java, FrameTypeAdapter())
        .create()
}

fun <T> toString(element: T): String {
    return element.toString()
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

fun fromWorker(task: (()-> Unit)?): Job {
    return GlobalScope.launch(Dispatchers.Default) {
        task?.invoke()
    }
}

fun fromScopeWorker(task: ((CoroutineScope)-> Unit)?): Job {
    return GlobalScope.launch(Dispatchers.Default) {
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

fun runSequentialReversed(delay: Long, times: Int, onEnd: (() -> Unit)? = null, applyAction: (index: Int) -> Unit) {
    if (times == 0)
        return

    GlobalScope.launch(Dispatchers.Main.immediate) {
        for (counter in (times - 1) downTo  0) {
            delay(delay)
            applyAction(counter)
        }
        delay(delay * 2)
        onEnd?.invoke()
    }
}