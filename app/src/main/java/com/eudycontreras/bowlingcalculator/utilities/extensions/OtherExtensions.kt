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

data class Ternary<T>(val target: T, val result: Boolean)

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

infix fun <T> Collection<T>.and(other: Collection<T>): Collection<T> {
    return this.plus(other)
}

infix fun <T> Collection<T>.without(other: Collection<T>): Collection<T> {
    return this.minus(other)
}

infix fun <T> Boolean.then(target: T): Ternary<T> {
    return Ternary(target, this)
}

infix fun <T> Ternary<T>.or(target: T): T {
    return if (this.result) this.target else target
}

inline fun <reified T> T.clone(): T {
    return gson.fromJson(gson.toJson(this, T::class.java), T::class.java)
}

inline fun <T> T.doWhen(block: T.() -> Boolean, action: (T)-> Unit ): Ternary<T> {
    if (block.invoke(this)) {
        action.invoke(this)
        return Ternary(this, true)
    }
    return Ternary(this, false)
}
