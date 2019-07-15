package com.eudycontreras.bowlingcalculator.libraries.morpher.drawables

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.SystemClock
import android.util.Log
import java.lang.StrictMath.min


class MorphTransitionDrawableBackup2(vararg drawables: Drawable?) : TransitionDrawable(drawables) {

    private var transitionType: TransitionType = TransitionType.NONE

    private var reverseTransition: Boolean = false
    
    private var startTimeMillis: Long = 0

    private var fromValue: Int = 0
    private var toValue: Int = 0

    private var currentDuration: Long = 0
    private var originalDuration: Long = 0

    private var alphaValue: Int = 0
    private var scaleValue: Float = 0f
    private var roationValue: Float = 0f

    private var _crossFade: Boolean = false

    var crossFade: Boolean
        get() = _crossFade
        set(value) {
            _crossFade = value
        }

    fun startTransition(durationMillis: Long) {
        fromValue = 0
        toValue = 255
        alphaValue = 0
        originalDuration = durationMillis
        currentDuration = originalDuration
        reverseTransition = false
        transitionType = TransitionType.STARTING
        invalidateSelf()
    }

    override fun startTransition(durationMillis: Int) {
        startTransition(durationMillis.toLong())
    }

    fun showFirstdLayer() {
        resetTransition()
    }

    fun showSecondLayer() {
        alphaValue = 255
        reverseTransition = false
        transitionType = TransitionType.NONE
        invalidateSelf()
    }

    override fun resetTransition() {
        alphaValue = 0
        transitionType = TransitionType.NONE
        invalidateSelf()
    }

    fun reverseTransition(duration: Long) {
        val time = SystemClock.uptimeMillis()

        if (time - startTimeMillis > currentDuration) {
            if (toValue == 0) {
                fromValue = 0
                toValue = 255
                alphaValue = 0
                reverseTransition = false
            } else {
                fromValue = 255
                toValue = 0
                alphaValue = 255
                reverseTransition = true
            }
            originalDuration = duration
            currentDuration = originalDuration
            transitionType = TransitionType.STARTING
            invalidateSelf()
            return
        }

        reverseTransition = !reverseTransition
        fromValue = alphaValue
        toValue = if (reverseTransition) 0 else 255
        currentDuration = if (reverseTransition) time - startTimeMillis else originalDuration - (time - startTimeMillis)
        transitionType = TransitionType.STARTING
    }

    override fun reverseTransition(duration: Int) {
        reverseTransition(duration.toLong())
    }

    fun updateTransition(fraction: Float) {
        Log.d("FRACTION::", fraction.toString())
    }

    override fun draw(canvas: Canvas) {
        var done = true

        when (transitionType) {
            TransitionType.STARTING -> {
                startTimeMillis = SystemClock.uptimeMillis()
                done = false
                transitionType = TransitionType.RUNNING
            }

            TransitionType.RUNNING -> if (startTimeMillis >= 0) {
                var normalized = (SystemClock.uptimeMillis() - startTimeMillis).toFloat() / currentDuration
                done = normalized >= 1.0f
                normalized = min(normalized, 1.0f)
                alphaValue = (fromValue + (toValue - fromValue) * normalized).toInt()
            }
            else -> {}
        }

        val alpha = alphaValue
        val crossFade = crossFade

        if (done) {
            if (!crossFade || alpha == 0) {
                getDrawable(0).draw(canvas)
            }
            if (alpha == 0xFF) {
                getDrawable(1).draw(canvas)
            }
            return
        }

        var drawable: Drawable = getDrawable(0)

        if (crossFade) {
            drawable.alpha = 255 - alpha
        }
        drawable.draw(canvas)
        if (crossFade) {
            drawable.alpha = 0xFF
        }

        if (alpha > 0) {
            drawable = getDrawable(1)
            drawable.alpha = alpha
            drawable.draw(canvas)
            drawable.alpha = 0xFF
        }

        if (!done) {
            invalidateSelf()
        }
    }

    override fun setCrossFadeEnabled(enabled: Boolean) {
        crossFade = enabled
    }

    override fun isCrossFadeEnabled(): Boolean {
        return crossFade
    }

    enum class TransitionType {
        STARTING, RUNNING, NONE
    }

}
