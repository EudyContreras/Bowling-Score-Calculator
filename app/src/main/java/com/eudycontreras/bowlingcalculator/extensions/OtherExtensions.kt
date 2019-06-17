package com.eudycontreras.bowlingcalculator.extensions

import android.app.Activity
import android.util.DisplayMetrics
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.eudycontreras.bowlingcalculator.Application
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll

/**
 * Created by eudycontreras.
 */

fun List<Roll>.sum() = map { it.totalKnockdown }.sum()

fun List<Frame>.getComputedScore(): Int {
    val scoreAccumulator = this
        .map { it.rolls.values }
        .flatten()
        .sumBy { it.totalKnockdown }

    val bonusAccumulator = this.map { it.bonusPoints }.sum()

    return scoreAccumulator + bonusAccumulator
}

val Activity.app: Application
    get() = application as Application

fun Activity.dimensions(): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()

    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    return Pair(width, height)
}

fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, func)
}

fun <T> List<T>.asLiveData(): LiveData<List<T>> {
    val livaData = MediatorLiveData<List<T>>()
    livaData.value = this
    return livaData
}
