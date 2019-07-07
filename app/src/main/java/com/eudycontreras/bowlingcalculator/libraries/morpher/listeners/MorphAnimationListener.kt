package com.eudycontreras.bowlingcalculator.libraries.morpher.listeners

import android.animation.Animator
import android.animation.AnimatorListenerAdapter

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
