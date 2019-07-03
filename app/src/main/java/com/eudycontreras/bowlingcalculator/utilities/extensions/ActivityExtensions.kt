package com.eudycontreras.bowlingcalculator.utilities.extensions

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.eudycontreras.bowlingcalculator.Application



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
    return ContextCompat.getColor(this, colorResId)
}

fun Activity.string(@StringRes stringResId: Int): String {
    return resources.getString(stringResId)
}

fun Activity.hideInput() {
    val view = this.currentFocus
    view?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Activity.showInput() {
    val view = this.currentFocus
    view?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
    }
}