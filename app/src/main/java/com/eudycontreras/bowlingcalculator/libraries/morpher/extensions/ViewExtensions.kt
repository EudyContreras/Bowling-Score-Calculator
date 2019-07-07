package com.eudycontreras.bowlingcalculator.libraries.morpher.extensions

import android.graphics.drawable.ColorDrawable
import android.view.View
import com.eudycontreras.bowlingcalculator.libraries.morpher.MorphTransitioner
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout

/**
 * <h1>Class description!</h1>
 *
 *
 *
 * **Note:** Unlicensed private property of the author and creator
 * unauthorized use of this class outside of the Soul Vibe project
 * may result on legal prosecution.
 *
 *
 * Created by <B>Eudy Contreras</B>
 *
 * @author  Eudy Contreras
 * @version 1.0
 * @since   2018-03-31
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

fun MorphLayout.getProperties(): MorphTransitioner.Properties {
    val x = this.morphX
    val y = this.morphY
    val width = this.morphWidth
    val height = this.morphHeight
    val alpha = this.morphAlpha
    val elevation = this.morphElevation
    val translationZ = this.morphTranslationZ
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
    val tag = this.morphTag.toString()
    return MorphTransitioner.Properties(
        x,
        y,
        width,
        height,
        alpha,
        elevation,
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
        tag
    )
}