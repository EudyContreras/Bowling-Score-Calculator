package com.eudycontreras.bowlingcalculator

import android.os.Handler


/**
 * Created by eudycontreras.
 */

fun <T> toString(element: T): String {
    return element.toString()
}

fun runAfter(delay: Long, action: ()-> Unit) {
    Handler().postDelayed(action,delay)
}

fun runSequential(delay: Long, times: Int, applyAction: (index: Int) -> Unit) {
    if (times == 0)
        return

    val thread = Thread(Runnable {
        for (counter in 0 until times) {
            applyAction(counter)
            waitTime(delay)
        }
    })
    thread.isDaemon = true
    thread.start()
}

fun waitTime(duration: Long) {
    try {
        Thread.sleep(duration)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
}