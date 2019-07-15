package com.eudycontreras.bowlingcalculator.libraries.morpher.drawables

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import com.eudycontreras.bowlingcalculator.utilities.mapRange


class MorphTransitionDrawableBackup3(vararg drawables: Drawable?) : TransitionDrawable(drawables) {

    private var reverseTransition: Boolean = false

    private var fromValue: Int = 0
    private var toValue: Int = 255

    private var alphaValue: Int = 0
    private var scaleValue: Float = 0f
    private var roationValue: Float = 0f

    private var lastFraction: Float = 0f
    private var fraction: Float = 0f

    private var mTransitionType: TransitionType = TransitionType.SEQUENTIAL

    var transitionType: TransitionType
        get() = mTransitionType
        set(value) {
            mTransitionType = value
        }

    private var mCrossfadePadding: Float = 0f

    var crossfadePadding: Float
        get() = mCrossfadePadding
        set(value) {
            if (value in 0f..1f) {
                mCrossfadePadding = value
            }
        }

    override fun startTransition(durationMillis: Int) {
        startTransition(durationMillis.toLong())
    }

    fun startTransition(durationMillis: Long) {

    }

    override fun reverseTransition(durationMillis: Int) {
        reverseTransition(durationMillis.toLong())
    }

    fun reverseTransition(durationMillis: Long) {

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
               fromValue = 255
               toValue = 0
               alphaValue = 255
               reverseTransition = true
           } else {
               lastFraction = 0f
               fraction = 0f
               fromValue = 0
               toValue = 255
               alphaValue = 0
               reverseTransition = true
           }
        } else {
            lastFraction = 0f
            fraction = 0f
            fromValue = 0
            toValue = 255
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
        alphaValue = (fromValue + (toValue - fromValue) * fraction).toInt()

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

    private var inFirstHalf: Boolean = true

    private fun animateSequential(canvas: Canvas, fraction: Float) {

        if (inFirstHalf) {
            val firstHalf =  mapRange(fraction, 0f, 0.5f, 0f, 1f, 0f, 1.0f)

            alphaValue = (fromValue + (toValue - fromValue) * firstHalf).toInt()

            val alpha = alphaValue

            val drawable: Drawable = getDrawable(if (reverseTransition) 1 else 0)

            drawable.alpha = MAX_ALPHA - alpha

            drawable.draw(canvas)

            drawable.alpha = MAX_ALPHA_HEX

            if (firstHalf >= 1f) {
                inFirstHalf = false
                lastFraction = 1f
                fromValue = 0
                toValue = 255
                alphaValue = 0
            }
            return
        }

        val secondHalf =  mapRange(fraction, 0.5f, 1f, 0f, 1f, 0f, 1.0f)

        alphaValue = (fromValue + (toValue - fromValue) * secondHalf).toInt()

        val alpha = alphaValue

        val drawable: Drawable = getDrawable(if (reverseTransition) 0 else 1)

        if (alpha > 0) {
            drawable.alpha = alpha
            drawable.draw(canvas)
            drawable.alpha = MAX_ALPHA_HEX
        }

    }

    override fun draw(canvas: Canvas) {
        when (transitionType) {
            TransitionType.CROSSFADE -> animateCrossfade(canvas, fraction)
            TransitionType.SEQUENTIAL -> animateSequential(canvas, fraction)
        }
    }

    override fun setCrossFadeEnabled(enabled: Boolean) {
      //  transitionType = TransitionType.CROSSFADE
    }

    override fun isCrossFadeEnabled(): Boolean {
        return transitionType == TransitionType.CROSSFADE
    }

    fun setSequentialFadeEnabled(enabled: Boolean) {
        transitionType = TransitionType.SEQUENTIAL
    }

    fun isSequentialFadeEnabled(): Boolean {
        return transitionType == TransitionType.SEQUENTIAL
    }

    enum class TransitionType {
        CROSSFADE, SEQUENTIAL
    }

    private companion object {
        const val MAX_ALPHA = 255
        const val MAX_ALPHA_HEX = 0xFF
    }
}
