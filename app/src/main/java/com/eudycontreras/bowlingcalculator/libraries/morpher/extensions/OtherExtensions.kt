package com.eudycontreras.bowlingcalculator.libraries.morpher.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.graphics.drawable.VectorDrawable
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.eudycontreras.bowlingcalculator.libraries.morpher.drawables.MorphTransitionDrawable
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii

/**
 * Copyright (C) 2019 Motion Morpher Project
 *
 **Note:** Unlicensed private property of the author and creator
 * unauthorized use of this class outside of the Motion Morpher project
 * or other projects to which the author has explicitly added this library
 * may result on legal prosecution.
 *
 * @Project Motion Morpher
 * @author Eudy Contreras.
 * @since March 2019
 */
fun FloatArray.apply(other: FloatArray): FloatArray {
    if (this.size == other.size) {
        for (index in 0 until other.size) {
            this[index] = other[index]
        }
    }
    return this
}

fun FloatArray.apply(cornerRadii: CornerRadii): FloatArray {
    if (this.size == cornerRadii.size) {
        for (index in 0 until cornerRadii.size) {
            this[index] = cornerRadii[index]
        }
    }
    return this
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    val arrayList = ArrayList<T>()
    arrayList.addAll(this)
    return arrayList
}

fun <T> Sequence<T>.toArrayList(): ArrayList<T> {
    val arrayList = ArrayList<T>()
    arrayList.addAll(this)
    return arrayList
}

fun Drawable.toBitmap(): Bitmap {
    return if (this is BitmapDrawable) {
        this.bitmap
    } else if (this is VectorDrawableCompat || this is VectorDrawable) {
        val bitmap = Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
        bitmap
    } else if (this is TransitionDrawable){
        val bitmap = Bitmap.createBitmap(this.getDrawable(0).intrinsicWidth, this.getDrawable(0).intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
        bitmap
    } else {
        throw IllegalArgumentException("unsupported drawable type")
    }
}

fun MorphLayout.getBackgroundType(): MorphTransitionDrawable.DrawableType {
    return when {
        this.hasVectorDrawable() -> MorphTransitionDrawable.DrawableType.VECTOR
        this.hasBitmapDrawable() -> MorphTransitionDrawable.DrawableType.BITMAP
        else -> MorphTransitionDrawable.DrawableType.OTHER
    }
}