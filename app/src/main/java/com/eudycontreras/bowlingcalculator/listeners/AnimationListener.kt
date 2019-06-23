package com.eudycontreras.bowlingcalculator.listeners

import android.animation.Animator
import android.animation.AnimatorListenerAdapter

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class AnimationListener(
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
