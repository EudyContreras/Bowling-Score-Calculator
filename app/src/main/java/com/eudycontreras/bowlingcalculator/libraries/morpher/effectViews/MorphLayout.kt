package com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews

import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.view.View
import android.view.ViewPropertyAnimator
import com.eudycontreras.bowlingcalculator.libraries.morpher.drawables.MorphTransitionDrawable
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii

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
interface MorphLayout {
    var morphX: Float
    var morphY: Float
    var morphWidth: Float
    var morphHeight: Float
    var morphAlpha: Float
    var morphElevation: Float
    var morphTranslationX: Float
    var morphTranslationY: Float
    var morphTranslationZ: Float
    var morphPivotX: Float
    var morphPivotY: Float
    var morphRotation: Float
    var morphRotationX: Float
    var morphRotationY: Float
    var morphScaleX: Float
    var morphScaleY: Float
    val morphColor: Int
    var morphStateList: ColorStateList?
    var morphCornerRadii: CornerRadii
    var morphVisibility: Int
    val morphChildCount: Int
    var mutateCorners: Boolean
    val morphShape: Int
    val morphTag: Any?
    val windowLocationX: Int
    val windowLocationY: Int
    var morphBackground: Drawable
    var view: View
    fun animator(): ViewPropertyAnimator
    fun updateLayout()
    fun hasChildren(): Boolean
    fun getChildViewAt(index: Int): View
    fun getChildren(): Sequence<View>
    fun hasVectorDrawable(): Boolean
    fun hasGradientDrawable(): Boolean
    fun hasBitmapDrawable(): Boolean
    fun hasMorphTransitionDrawable(): Boolean
    fun getGradientBackground(): GradientDrawable
    fun getVectorDrawable(): VectorDrawable
    fun getBitmapDrawable(): BitmapDrawable
    fun getMorphTransitionDrawable(): MorphTransitionDrawable
    fun applyTransitionDrawable(transitionDrawable: MorphTransitionDrawable)
    fun applyDrawable(shape: Int, topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float)
    fun updateCorners(cornerRadii: CornerRadii): Boolean
    fun updateCorners(index: Int, corner: Float): Boolean
    fun getMorphShape(): MorphShape
    fun setLayer(layer: Int)
}