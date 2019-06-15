package com.eudycontreras.bowlingcalculator.utilities

import androidx.lifecycle.LiveData

/**
 * An Empty live data to use in places where one would typically return null
 */
class AbsentLiveData<T> private constructor() : LiveData<T>() {
    init {
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> = AbsentLiveData()
    }
}
