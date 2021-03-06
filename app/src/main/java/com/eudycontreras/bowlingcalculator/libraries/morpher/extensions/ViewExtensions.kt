package com.eudycontreras.bowlingcalculator.libraries.morpher.extensions

import android.graphics.drawable.ColorDrawable
import android.view.View
import com.eudycontreras.bowlingcalculator.libraries.morpher.Morpher
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout

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
fun View.getColor(default: Int = 0x000000): Int {
    val background =  this.backgroundTintList
    if (background != null) {
        return background.defaultColor
    } else {
        val drawable = this.background

        if (drawable is ColorDrawable) {
            return drawable.color
        }
    }
    return default
}

fun MorphLayout.getProperties(): Morpher.Properties {
    val x = this.morphX
    val y = this.morphY
    val width = this.morphWidth
    val height = this.morphHeight
    val alpha = this.morphAlpha
    val elevation = this.morphElevation
    val translationX = this.morphTranslationX
    val translationY = this.morphTranslationY
    val translationZ = this.morphTranslationZ
    val locationX = this.windowLocationX
    val locationY = this.windowLocationY
    val pivotX = this.morphPivotX
    val pivotY = this.morphPivotY
    val rotation = this.morphRotation
    val rotationX = this.morphRotationX
    val rotationY = this.morphRotationY
    val scaleX = this.morphScaleX
    val scaleY = this.morphScaleY
    val color = this.morphColor
    val stateList = this.morphStateList
    val cornerRadii = this.morphCornerRadii.getCopy()
    val background = this.morphBackground.constantState?.newDrawable()
    val hasVectorBackground = this.hasVectorDrawable()
    val hasBitmapBackground = this.hasBitmapDrawable()
    val hasGradientBackground = this.hasGradientDrawable()
    val tag = this.morphTag.toString()
    return Morpher.Properties(
        x,
        y,
        width,
        height,
        alpha,
        elevation,
        translationX,
        translationY,
        translationZ,
        pivotX,
        pivotY,
        rotation,
        rotationX,
        rotationY,
        scaleX,
        scaleY,
        color,
        stateList,
        cornerRadii,
        locationX,
        locationY,
        background,
        hasVectorBackground,
        hasBitmapBackground,
        hasGradientBackground,
        tag
    )
}