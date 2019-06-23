package com.eudycontreras.bowlingcalculator.utilities.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.eudycontreras.bowlingcalculator.calculator.ScoreCalculator
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Roll
import com.eudycontreras.bowlingcalculator.utilities.gson

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

fun List<Roll>.sum() = map { it.totalKnockdown }.sum()

fun Bowler.getComputedScore(): Int {
    return ScoreCalculator.getTotalScore(this)
}

fun Bowler.getPossibleScore(): Int {
    return ScoreCalculator.getPossibleScore(this)
}

fun <X, Y> LiveData<X>.switchMap(func : (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, func)
}

fun <T> List<T>.asLiveData(): MediatorLiveData<List<T>> {
    val livaData = MediatorLiveData<List<T>>()
    livaData.value = this
    return livaData
}

inline fun <reified T> T.clone(): T {
    return gson.fromJson(gson.toJson(this, T::class.java), T::class.java)
}