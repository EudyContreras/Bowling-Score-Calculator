package com.eudycontreras.bowlingcalculator.libraries.morpher.drawables

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.SystemClock


class MorphTransitionDrawableBackup(vararg drawables: Drawable?) : TransitionDrawable(drawables) {

    private var mTransitionState = TRANSITION_NONE

    private var mReverse: Boolean = false
    private var mStartTimeMillis: Long = 0

    private var mFrom: Int = 0
    private var mTo: Int = 0

    private var mDuration: Int = 0
    private var mOriginalDuration: Int = 0
    private var mAlpha = 0
    private var mCrossFade: Boolean = false

    /**
     * Begin the second layer on top of the first layer.
     *
     * @param durationMillis The length of the transition in milliseconds
     */
    override fun startTransition(durationMillis: Int) {
        mFrom = 0
        mTo = 255
        mAlpha = 0
        mOriginalDuration = durationMillis
        mDuration = mOriginalDuration
        mReverse = false
        mTransitionState = TRANSITION_STARTING
        invalidateSelf()
    }

    /**
     * Show the second layer on top of the first layer immediately
     *
     * @hide
     */
    fun showSecondLayer() {
        mAlpha = 255
        mReverse = false
        mTransitionState = TRANSITION_NONE
        invalidateSelf()
    }

    /**
     * Show only the first layer.
     */
    override fun resetTransition() {
        mAlpha = 0
        mTransitionState = TRANSITION_NONE
        invalidateSelf()
    }

    /**
     * Reverses the transition, picking up where the transition currently is.
     * If the transition is not currently running, this will start the transition
     * with the specified duration. If the transition is already running, the last
     * known duration will be used.
     *
     * @param duration The duration to use if no transition is running.
     */
    override fun reverseTransition(duration: Int) {
        val time = SystemClock.uptimeMillis()
        // Animation is over
        if (time - mStartTimeMillis > mDuration) {
            if (mTo == 0) {
                mFrom = 0
                mTo = 255
                mAlpha = 0
                mReverse = false
            } else {
                mFrom = 255
                mTo = 0
                mAlpha = 255
                mReverse = true
            }
            mOriginalDuration = duration
            mDuration = mOriginalDuration
            mTransitionState = TRANSITION_STARTING
            invalidateSelf()
            return
        }

        mReverse = !mReverse
        mFrom = mAlpha
        mTo = if (mReverse) 0 else 255
        mDuration = (if (mReverse) time - mStartTimeMillis else mOriginalDuration - (time - mStartTimeMillis)).toInt()
        mTransitionState = TRANSITION_STARTING
    }

    override fun draw(canvas: Canvas) {
        var done = true

        when (mTransitionState) {
            TRANSITION_STARTING -> {
                mStartTimeMillis = SystemClock.uptimeMillis()
                done = false
                mTransitionState = TRANSITION_RUNNING
            }

            TRANSITION_RUNNING -> if (mStartTimeMillis >= 0) {
                var normalized = (SystemClock.uptimeMillis() - mStartTimeMillis).toFloat() / mDuration
                done = normalized >= 1.0f
                normalized = Math.min(normalized, 1.0f)
                mAlpha = (mFrom + (mTo - mFrom) * normalized).toInt()
            }
        }

        val alpha = mAlpha
        val crossFade = mCrossFade

        if (done) {
            // the setAlpha() calls below trigger invalidation and redraw. If we're done, just draw
            // the appropriate drawable[s] and return
            if (!crossFade || alpha == 0) {
                getDrawable(0).draw(canvas)
            }
            if (alpha == 0xFF) {
                getDrawable(1).draw(canvas)
            }
            return
        }

        var d: Drawable
        d = getDrawable(0)
        if (crossFade) {
            d.alpha = 255 - alpha
        }
        d.draw(canvas)
        if (crossFade) {
            d.alpha = 0xFF
        }

        if (alpha > 0) {
            d = getDrawable(1)
            d.alpha = alpha
            d.draw(canvas)
            d.alpha = 0xFF
        }

        if (!done) {
            invalidateSelf()
        }
    }

    /**
     * Enables or disables the cross fade of the drawables. When cross fade
     * is disabled, the first drawable is always drawn opaque. With cross
     * fade enabled, the first drawable is drawn with the opposite alpha of
     * the second drawable. Cross fade is disabled by default.
     *
     * @param enabled True to enable cross fading, false otherwise.
     */
    override fun setCrossFadeEnabled(enabled: Boolean) {
        mCrossFade = enabled
    }

    /**
     * Indicates whether the cross fade is enabled for this transition.
     *
     * @return True if cross fading is enabled, false otherwise.
     */
    override fun isCrossFadeEnabled(): Boolean {
        return mCrossFade
    }

    companion object {

        /**
         * A transition is about to start.
         */
        private val TRANSITION_STARTING = 0

        /**
         * The transition has started and the animation is in progress
         */
        private val TRANSITION_RUNNING = 1

        /**
         * No transition will be applied
         */
        private val TRANSITION_NONE = 2
    }

}
