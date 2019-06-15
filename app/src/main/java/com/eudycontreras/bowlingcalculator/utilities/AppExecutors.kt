package com.eudycontreras.bowlingcalculator.utilities

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class AppExecutors(
    private val diskIO: Executor,
    private val mainThread: Executor
) {

    companion object {
        private val INSTANCE = AppExecutors(
            Executors.newSingleThreadExecutor(),
            MainThreadExecutor()
        )

        fun instance() = INSTANCE
    }

    fun ioThread(task: () -> Unit) {
        diskIO.execute(task)
    }

    fun mainThread(task: () -> Unit) {
        mainThread.execute(task)
    }

    class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}
