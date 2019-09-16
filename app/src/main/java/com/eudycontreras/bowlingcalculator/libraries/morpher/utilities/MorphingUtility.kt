package com.eudycontreras.bowlingcalculator.libraries.morpher.utilities

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.ViewGroup
import android.view.animation.Interpolator

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
object MorphingUtility {

    private const val SCALE_X_PROPERTY = "scaleX"
    private const val SCALE_Y_PROPERTY = "scaleY"


    fun getLayoutTransition(
        viewGroup: ViewGroup,
        duration: Long,
        interpolator: Interpolator? = null
    ): LayoutTransition {
        return getLayoutTransition(viewGroup, duration, interpolator, interpolator)
    }

    fun getLayoutTransition(
        viewGroup: ViewGroup,
        duration: Long,
        scaleUpInterpolator: Interpolator?,
        scaleDownInterpolator: Interpolator?
    ): LayoutTransition {

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            viewGroup,
            PropertyValuesHolder.ofFloat(SCALE_X_PROPERTY, 1f, 0f),
            PropertyValuesHolder.ofFloat(SCALE_Y_PROPERTY, 1f, 0f)
        )
        scaleDown.duration = duration

        if (scaleDownInterpolator != null)
            scaleDown.interpolator = scaleDownInterpolator

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            viewGroup,
            PropertyValuesHolder.ofFloat(SCALE_X_PROPERTY, 0f, 1f),
            PropertyValuesHolder.ofFloat(SCALE_Y_PROPERTY, 0f, 1f)
        )

        scaleUp.duration = duration

        if (scaleUpInterpolator != null)
            scaleUp.interpolator = scaleUpInterpolator

        viewGroup.layoutTransition = LayoutTransition()

        val itemLayoutTransition = viewGroup.layoutTransition

        itemLayoutTransition.setAnimator(LayoutTransition.APPEARING, scaleUp)
        itemLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, scaleDown)
        itemLayoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, scaleUp)
        itemLayoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, scaleDown)
        itemLayoutTransition.setAnimator(LayoutTransition.CHANGING, scaleUp)

        itemLayoutTransition.enableTransitionType(LayoutTransition.APPEARING)
        itemLayoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING)
        itemLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        itemLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)
        itemLayoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        itemLayoutTransition.setAnimateParentHierarchy(true)

        return itemLayoutTransition
    }
}
