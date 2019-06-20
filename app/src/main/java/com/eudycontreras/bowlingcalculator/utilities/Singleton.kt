package com.eudycontreras.bowlingcalculator.utilities

open class Singleton<out T: Any>(creator: () -> T) {
    private var creator: (() -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(): T {
        val localInstance = instance
        if (localInstance != null) {
            return localInstance
        }

        return synchronized(this) {
            val localSync = instance
            if (localSync != null) {
                localSync
            } else {
                val created = creator!!()
                instance = created
                creator = null
                created
            }
        }
    }
}