package com.eudycontreras.bowlingcalculator.libraries.morpher.listeners

import android.animation.Animator
import android.animation.AnimatorListenerAdapter

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
class MorphAnimationListener(
    var onStart: (() -> Unit)? = null,
    var onEnd: (() -> Unit)? = null
) : AnimatorListenerAdapter() {

    override fun onAnimationStart(animation: Animator?) {
        super.onAnimationStart(animation)
        onStart?.invoke()
    }

    override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)
        onEnd?.invoke()
    }
}
