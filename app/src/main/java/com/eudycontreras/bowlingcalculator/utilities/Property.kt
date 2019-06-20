package com.eudycontreras.bowlingcalculator.utilities

/******************************************************************
 *
 *
 *
 * @author Eudy Contreras.
 * @since 6/20/19
 ******************************************************************/
data class Property<T>(var value: T) {

    @Synchronized
    fun set(value: T) {
        this.value = value
    }

    fun get(): T = value
}