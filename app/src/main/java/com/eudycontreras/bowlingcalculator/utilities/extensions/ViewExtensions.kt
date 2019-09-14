package com.eudycontreras.bowlingcalculator.utilities.extensions

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.EditText

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

fun View.detach() {
    this.visibility = View.GONE
}

fun View.attach() {
    this.visibility = View.VISIBLE
}

fun View.hide(duration: Long = 0L) {
    if (duration == 0L){
        this.alpha = 0f
        return
    }
    this.animate()
        .alpha(0f)
        .setDuration(duration)
        .start()
}

fun View.popIn(duration: Long = 0L) {
    if (duration == 0L){
        this.scaleX = 0f
        this.scaleY = 0f
        return
    }
    this.animate()
        .scaleX(0f)
        .scaleY(0f)
        .setDuration(duration)
        .start()
}

fun View.show(duration: Long = 0L) {
    if (duration == 0L){
        this.alpha = 1f
        return
    }
    this.animate()
        .alpha(1f)
        .setDuration(duration)
        .start()
}

fun View.setSize(width: Float, height: Float) {
    val params = this.layoutParams
    params.width = width.toInt()
    params.height = height.toInt()
    this.layoutParams = params
    this.requestLayout()
}

fun EditText.AddChangeListener(
    onAfterChange: ((String) -> Unit)? = null,
    onBeforeChange: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(sequence: Editable) { onAfterChange?.invoke(sequence.toString()) }

        override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) { onBeforeChange?.invoke(sequence.toString()) }

        override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) { onChange?.invoke(sequence.toString()) }
    })
}

fun View.addTouchAnimation(
    clickTarget: View? = null,
    durationPress: Long = 150,
    durationRelease: Long = 200,
    scale: Float = 0.97f,
    depth: Float = 0f,
    originalDepth: Float = -1F,
    interpolatorPress: Interpolator? = null,
    interpolatorRelease: Interpolator? = null,
    directFeedback: Boolean = true,
    gestureDetector: GestureDetector? = null
) {

    /*val releaseListener = AnimationListener(
        onEnd = {
            if (!directFeedback) {
                if (clickTarget != null) {
                    clickTarget.performClick()
                } else {
                    this.performClick()
                }
            }
        }
    )
*/
    val lastDepth = if (originalDepth == -1F) this.translationZ else originalDepth

    this.setOnTouchListener { _, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                this.animate()
                    .setStartDelay(0)
                    .setInterpolator(interpolatorPress)
                    .setListener(null)
                    .translationZ(depth)
                    .scaleY(scale)
                    .scaleX(scale)
                    .setDuration(durationPress)
                    .start()
            }
            MotionEvent.ACTION_UP -> {
                this.animate()
                    .setStartDelay(0)
                    .setInterpolator(interpolatorRelease)
                    //.setListener(releaseListener)
                    .setListener(null)
                    .translationZ(lastDepth)
                    .scaleY(1f)
                    .scaleX(1f)
                    .setDuration(durationRelease)
                    .start()

                if (directFeedback) {
                    this.performClick()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                this.animate()
                    .setStartDelay(0)
                    .setInterpolator(interpolatorRelease)
                    .setListener(null)
                    .translationZ(lastDepth)
                    .scaleY(1f)
                    .scaleX(1f)
                    .setDuration(durationRelease)
                    .start()
            }
            MotionEvent.ACTION_CANCEL -> {
                this.animate()
                    .setStartDelay(0)
                    .setInterpolator(interpolatorRelease)
                    .setListener(null)
                    .translationZ(lastDepth)
                    .scaleY(1f)
                    .scaleX(1f)
                    .setDuration(durationRelease)
                    .start()
            }
            else -> {}
        }
        gestureDetector?.onTouchEvent(motionEvent) ?: true
    }
}

fun View.animateColor(from: Int, to: Int, duration: Long) {
    if (duration == 0L) {
        this.backgroundTintList = ColorStateList.valueOf(to)
        return
    }
    val colorAnimation = ValueAnimator.ofArgb(from, to)
    colorAnimation.duration = duration
    colorAnimation.addUpdateListener { anim ->
        val value = ColorStateList.valueOf(anim.animatedValue as Int)
        this.backgroundTintList = value
    }
    colorAnimation.start()
}

fun View.animateColor(from: ColorStateList, to: ColorStateList, duration: Long) {
    this.animateColor(from.defaultColor, to.defaultColor, duration)
}
