package com.eudycontreras.bowlingcalculator.utilities.extensions

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.eudycontreras.bowlingcalculator.Application
import com.eudycontreras.bowlingcalculator.R

val Activity.app: Application
    get() = application as Application

fun Activity.dimensions(): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()

    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    return Pair(width, height)
}

fun Activity.drawable(@DrawableRes iconResId: Int): Drawable {
    return ContextCompat.getDrawable(this, iconResId)!!
}

fun Activity.color(@ColorRes colorResId: Int): Int {
    return ContextCompat.getColor(this, R.color.colorAccent)
}