package com.eudycontreras.bowlingcalculator.libraries.morpher.drawables

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import androidx.core.animation.doOnEnd
import com.eudycontreras.bowlingcalculator.libraries.morpher.Action
import com.eudycontreras.bowlingcalculator.utilities.mapRange
import java.util.*


class MorphTransitionDrawable(vararg drawables: Drawable?) : TransitionDrawable(drawables) {

    private var reverseTransition: Boolean = false

    private var mFromAlpha: Int = 0
    private var mToAlpha: Int = 255

    private var mFromAngle: Int = 0
    private var mToAngle: Int = 180

    private var mFromScaleY: Float = 0f
    private var mFromScaleX: Float = 0f

    private var mToScaleX: Float = 1f
    private var mToScaleY: Float = 1f

    private var alphaValue: Int = 0
    private var scaleValueX: Float = 0f
    private var scaleValueY: Float = 0f
    private var rotationValue: Float = 0f

    private var lastFraction: Float = 0f
    private var fraction: Float = 0f

    private var mCrossfadePadding: Float = 0f

    private var mTransitionType: TransitionType = TransitionType.SEQUENTIAL

    var isSequentialFadeEnabled: Boolean
        get() = transitionType == TransitionType.SEQUENTIAL
        set(value) {
            transitionType = if (value) TransitionType.SEQUENTIAL else transitionType
        }

    var transitionType: TransitionType
        get() = mTransitionType
        set(value) {
            mTransitionType = value
        }

    var crossfadePadding: Float
        get() = mCrossfadePadding
        set(value) {
            if (value in 0f..1f) {
                mCrossfadePadding = value
            }
        }

    var fromAlpha: Float
        get() = mFromAlpha / 255f
        set(value) {
            if (value in 0f..1f) {
                mFromAlpha = (255 * value).toInt()
            }
        }

    var toAlpha: Float
        get() = mToAlpha / 255f
        set(value) {
            if (value in 0f..1f) {
                mToAlpha = (255 * value).toInt()
            }
        }


    var fromAngle: Int
        get() = mFromAngle
        set(value) {
            if (value in 0..360) {
                mFromAngle = value
            }
        }

    var toAngle: Int
        get() = mToAngle
        set(value) {
            if (value in 0..360) {
                mToAngle = value
            }
        }

    var fromScaleX: Float
        get() = mFromScaleX
        set(value) {
            if (value in 0f..1f) {
                mFromScaleX = value
            }
        }

    var toScaleX: Float
        get() = mToScaleX
        set(value) {
            if (value in 0f..1f) {
                mToScaleX = value
            }
        }

    var fromScaleY: Float
        get() = mFromScaleY
        set(value) {
            if (value in 0f..1f) {
                mFromScaleY = value
            }
        }

    var toScaleY: Float
        get() = mToScaleY
        set(value) {
            if (value in 0f..1f) {
                mToScaleY = value
            }
        }

    override fun startTransition(durationMillis: Int) {
        startTransition(durationMillis.toLong())
    }

    fun startTransition(durationMillis: Long, interpolator: TimeInterpolator? = null, onEnd: Action = null): ValueAnimator {
        setUpTransition(false)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.interpolator = interpolator
        animator.duration = durationMillis
        animator.doOnEnd { onEnd?.invoke() }
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            updateTransition(value)
        }
        animator.start()
        return animator
    }

    override fun reverseTransition(durationMillis: Int) {
        reverseTransition(durationMillis.toLong())
    }

    fun reverseTransition(durationMillis: Long, interpolator: TimeInterpolator? = null, onEnd: Action = null): ValueAnimator {
        setUpTransition(true)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.interpolator = interpolator
        animator.duration = durationMillis
        animator.doOnEnd { onEnd?.invoke() }
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            updateTransition(value)
        }
        animator.start()
        return animator
    }

    fun showFirstdLayer() {
        alphaValue = 0
        invalidateSelf()
    }

    fun showSecondLayer() {
        alphaValue = 255
        reverseTransition = false
        invalidateSelf()
    }

    override fun resetTransition() {
        alphaValue = 0
        invalidateSelf()
    }

    fun setUpTransition(useReverse: Boolean = false) {
        if (useReverse) {
           if (transitionType == TransitionType.CROSSFADE) {
               lastFraction = 1f
               fraction = 1f

               mFromAlpha = 255
               mToAlpha = 0

               mFromAngle = 180
               mToAngle = 0

               mFromScaleX = 0f
               mFromScaleY = 0f

               mToScaleX = 1f
               mToScaleY = 1f

               rotationValue = 180f
               scaleValueX = 0f
               scaleValueY = 0f
               alphaValue = 255

               reverseTransition = true
           } else {
               lastFraction = 0f
               fraction = 0f

               mFromAlpha = 0
               mToAlpha = 255

               mFromAngle = 180
               mToAngle = 0

               mFromScaleX = 1f
               mFromScaleY = 1f

               mToScaleX = 0f
               mToScaleY = 0f

               rotationValue = 180f
               scaleValueX = 1f
               scaleValueY = 1f
               alphaValue = 0

               reverseTransition = true
           }
        } else {
            lastFraction = 0f
            fraction = 0f

            mFromAlpha = 0
            mToAlpha = 255

            mFromAngle = 0
            mToAngle = 180

            mFromScaleX = 1f
            mFromScaleY = 1f

            mToScaleX = 0f
            mToScaleY = 0f

            rotationValue = 0f
            scaleValueX = 1f
            scaleValueY = 1f
            alphaValue = 0

            reverseTransition = false
        }
        inFirstHalf = true
    }

    fun updateTransition(inFraction: Float) {
        fraction = mapRange(inFraction, mCrossfadePadding, 1.0f - mCrossfadePadding, 0f, 1f, 0f, 1.0f)

        if (lastFraction > 0f && lastFraction < 1f) {
            invalidateSelf()
        }

        lastFraction = fraction
    }

    private fun animateCrossfade(canvas: Canvas, fraction: Float) {
        fun animateScale(value: Float) {

        }

        fun animateRotate(value: Float) {

        }

        fun animateFade() {
            alphaValue = (mFromAlpha + (mToAlpha - mFromAlpha) * fraction).toInt()

            val alpha = alphaValue

            var drawable: Drawable = getDrawable(0)

            drawable.alpha = MAX_ALPHA - alpha

            drawable.draw(canvas)

            drawable.alpha = MAX_ALPHA_HEX

            if (alpha > 0) {
                drawable = getDrawable(1)
                drawable.alpha = alpha
                drawable.draw(canvas)
                drawable.alpha = MAX_ALPHA_HEX
            }
        }

        animateFade()
    }

    private var inFirstHalf: Boolean = true

    private fun animateSequential(canvas: Canvas, fraction: Float) {

        fun animateStartScale(value: Float) {
            scaleValueX = (mFromScaleX + (mToScaleX - mFromScaleX) * value)
            scaleValueY = (mFromScaleY + (mToScaleY - mFromScaleY) * value)

            val drawable: Drawable = getDrawable(if (reverseTransition) 1 else 0)

            canvas.scale(scaleValueX, scaleValueY,drawable.intrinsicWidth / 2f, drawable.intrinsicHeight / 2f)
        }

        fun animateEndScale(value: Float) {
            scaleValueX = (mFromScaleX + (mToScaleX - mFromScaleX) * value)
            scaleValueY = (mFromScaleY + (mToScaleY - mFromScaleY) * value)

            val drawable: Drawable = getDrawable(if (reverseTransition) 0 else 1)

            canvas.scale(scaleValueX, scaleValueY,drawable.intrinsicWidth / 2f, drawable.intrinsicHeight / 2f)
        }

        fun animateRotate(value: Float) {
            rotationValue = (mFromAngle + (mToAngle - mFromAngle) * value)

            val drawable: Drawable = getDrawable(if (reverseTransition) 1 else 0)

            canvas.rotate(rotationValue, drawable.intrinsicWidth / 2f, drawable.intrinsicHeight / 2f)
        }

        fun animateStartFade(value: Float) {
            alphaValue = (mFromAlpha + (mToAlpha - mFromAlpha) * value).toInt()

            val alpha = alphaValue

            val drawable: Drawable = getDrawable(if (reverseTransition) 1 else 0)

            drawable.alpha = MAX_ALPHA - alpha

            drawable.draw(canvas)

            drawable.alpha = MAX_ALPHA_HEX

            if (value >= 1f) {
                inFirstHalf = false
                lastFraction = 1f

                mFromAlpha = 0
                mToAlpha = 255

                mFromScaleX = 1f
                mFromScaleY = 1f

                mToScaleX = 0f
                mToScaleY = 0f

                scaleValueX = 1f
                scaleValueY = 1f

                alphaValue = 0
            }
        }

        fun animateEndFade(value: Float) {

            alphaValue = (mFromAlpha + (mToAlpha - mFromAlpha) * value).toInt()

            val alpha = alphaValue

            val drawable: Drawable = getDrawable(if (reverseTransition) 0 else 1)

            if (alpha > 0) {
                drawable.alpha = alpha
                drawable.draw(canvas)
                drawable.alpha = MAX_ALPHA_HEX
            }
        }

        animateRotate(fraction)

        if (inFirstHalf) {
            val value = mapRange(
                value = fraction,
                fromMin = 0f,
                fromMax = 0.5f,
                toMin = 0f,
                toMax = 1f,
                clampMin = 0f,
                clampMax = 1.0f)

            animateStartFade(value)
            animateStartScale(value)
            return
        }

        val value = mapRange(
            value = fraction,
            fromMin = 0.5f,
            fromMax = 1f,
            toMin = 0f,
            toMax = 1f,
            clampMin = 0f,
            clampMax = 1.0f)

        animateEndFade(value)
        animateEndScale(value)
    }

    override fun draw(canvas: Canvas) {
        when (transitionType) {
            TransitionType.CROSSFADE -> animateCrossfade(canvas, fraction)
            TransitionType.SEQUENTIAL -> animateSequential(canvas, fraction)
        }
    }

    override fun setCrossFadeEnabled(enabled: Boolean) {
        transitionType = TransitionType.CROSSFADE
    }

    override fun isCrossFadeEnabled(): Boolean {
        return transitionType == TransitionType.CROSSFADE
    }

    override fun isStateful(): Boolean {
        return true
    }

    override fun setState(stateSet: IntArray): Boolean {
        if (!Arrays.equals(state, stateSet)) {
            state = stateSet
            return onStateChange(stateSet)
        }
        return false
    }

    enum class TransitionType {
        CROSSFADE, SEQUENTIAL
    }

    companion object {
        private const val MAX_ALPHA = 255
        private const val MAX_ALPHA_HEX = 0xFF
    }

    data class ValueMap<T>(
        var fromValue: T,
        var toValue: T
    )

    interface Decoration {
        var fractionStart: Float
        var fractionEnd: Float
        fun draw(canvas: Canvas, fraction: Float)
    }
}
