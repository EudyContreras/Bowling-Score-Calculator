package com.eudycontreras.bowlingcalculator.libraries.morpher.utilities

import android.animation.*
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.effects.RectangularReveal
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.MorphAnimationListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Dimension
import com.eudycontreras.bowlingcalculator.utilities.Action
import com.eudycontreras.bowlingcalculator.utilities.doWith
import com.eudycontreras.bowlingcalculator.utilities.extensions.show
import kotlin.math.abs
import kotlin.math.hypot

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
class MorphingUtility {

    private lateinit var sourceView: View
    private lateinit var dialogView: ViewGroup

    private var locationStart: IntArray? = null
    private var locationEnd: IntArray? = null

    private var isMorphing = false
    private var isMorphed = false

    private var setUp = false

    private var dimensionFrom: Dimension? = null
    private var dimensionTo: Dimension? = null

    var interpolatorMorphTo: Interpolator? = null
    var interpolatorMorphFrom: Interpolator? = null

    var fromRadius = 1000f
    var toRadius = 24f

    var colorFrom: Int = 0
    var colorTo: Int = 0

    init {

        this.interpolatorMorphTo = DecelerateInterpolator()
        this.interpolatorMorphFrom = DecelerateInterpolator()
    }

    fun setDialogView(dialogView: ViewGroup) {
        this.dialogView = dialogView
    }

    fun setSourceView(sourceView: View) {
        this.sourceView = sourceView
    }

    private fun setUp() {
        if (setUp)
            return

        doWith(sourceView, dialogView) { from, to ->
            dimensionTo = dimensionTo?:Dimension(to.measuredWidth.toFloat(), to.measuredHeight.toFloat())
            dimensionFrom = dimensionFrom?: Dimension(from.measuredWidth.toFloat(), from.measuredHeight.toFloat())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            val toBackground = dialogView.background as GradientDrawable
            val fromBackground = sourceView.background as GradientDrawable

            fromRadius = fromBackground.cornerRadius
            toRadius = toBackground.cornerRadius

            if (fromRadius == 0f) {
                var highest = 0f

                fromBackground.cornerRadii?.let {
                    for (i in 0 until it.size) {
                        val corner = it[i]
                        if (corner > highest) {
                            highest = corner
                        }
                    }
                    fromRadius = highest
                }
            }
        }

        setUp = true
    }

    fun morphIntoDialog(duration: Long, onStart: Action, onEnd: Action) {

        setUp()

        isMorphing = true

        val endScript = {
            onEnd?.invoke()

            isMorphing = false
            isMorphed = true

            val layoutParams = dialogView.layoutParams as FrameLayout.LayoutParams
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            dialogView.layoutParams = layoutParams


            for (i in 0 until dialogView.childCount) {
                val child = dialogView.getChildAt(i)

                child.alpha = 1f
            }
        }

        for (i in 0 until dialogView.childCount) {
            val child = dialogView.getChildAt(i)

            if (child.visibility == View.GONE)
                continue

            child.alpha = 1f
            child.visibility = View.VISIBLE
        }

        val layoutParams = dialogView.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = dimensionFrom!!.width.toInt()
        layoutParams.height = dimensionFrom!!.height.toInt()

        dialogView.visibility = View.VISIBLE
        sourceView.visibility = View.INVISIBLE

        sourceView.isEnabled = false

        if (locationStart == null) {
            locationStart = IntArray(2)
            locationEnd = IntArray(2)

            sourceView.getLocationInWindow(locationStart)
            dialogView.getLocationInWindow(locationEnd)
        }

        val startX: Float = locationStart!![0] - (dimensionTo!!.width / 2f - dimensionFrom!!.width / 2f)
        val startY: Float = locationStart!![1] - (dimensionTo!!.height / 2f - dimensionFrom!!.height / 2f)

        val endX: Float = locationEnd!![0].toFloat()
        val endY: Float = locationEnd!![1].toFloat()

        val translationX: Float = abs(endX - startX)
        val translationY: Float = abs(endY - startY)

        dialogView.translationX = if (startX < endX) -translationX else translationX
        dialogView.translationY = if (startY < endY) -translationY else translationY

        morph(
            dialogView,
            interpolatorMorphTo,
            duration,
            colorFrom,
            colorTo,
            dimensionTo!!.width.toInt(),
            dimensionTo!!.height.toInt(),
            fromRadius,
            toRadius,
            onStart,
            endScript
        )

        dialogView.animate()
            .alpha(1f)
            .translationX(0f)
            .translationY(0f)
            .setInterpolator(interpolatorMorphTo)
            .setDuration(duration)
            .start()
    }


    fun morphFromDialog(duration: Long, onStart: Action, onEnd: Action) {
        isMorphing = true

        val endScript = {
            onEnd?.invoke()

            sourceView.visibility = View.VISIBLE
            dialogView.visibility = View.INVISIBLE

            val layoutParams = dialogView.layoutParams as RelativeLayout.LayoutParams
            layoutParams.width = dimensionTo!!.width.toInt()
            layoutParams.height = dimensionTo!!.height.toInt()

            dialogView.layoutParams = layoutParams
            dialogView.translationX = 0f
            dialogView.translationY = 0f
            sourceView.isEnabled = true

            isMorphing = false
            isMorphed = false
        }

        val layoutParams = dialogView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.width = dimensionTo!!.width.toInt()
        layoutParams.height = dimensionTo!!.height.toInt()

        dialogView.layoutParams = layoutParams

        if (locationStart == null) {
            locationStart = IntArray(2)
            locationEnd = IntArray(2)

            dialogView.getLocationOnScreen(locationStart)
            sourceView.getLocationOnScreen(locationEnd)
        }

        val startX = locationStart!![0].toFloat()
        val startY = locationStart!![1].toFloat()

        val endX = locationEnd!![0].toFloat()
        val endY = locationEnd!![1].toFloat()

        val translationX = abs(endX - startX)
        val translationY = abs(endY - startY)

        morph(
            dialogView,
            interpolatorMorphFrom,
            duration,
            colorTo,
            colorFrom,
            dimensionFrom!!.width.toInt(),
            dimensionFrom!!.height.toInt(),
            toRadius,
            fromRadius,
            onStart,
            endScript
        )

        dialogView.animate()
            .alpha(1f)
            .translationX(if (startX < endX) -translationX else translationX)
            .translationY(if (startY < endY) -translationY else translationY)
            .setInterpolator(interpolatorMorphFrom)
            .setDuration(duration)
            .start()
    }

    companion object {

        private const val CORNER_RADIUS_PROPERTY = "cornerRadius"

        private const val SCALE_X_PROPERTY = "scaleX"
        private const val SCALE_Y_PROPERTY = "scaleY"

        fun morph(
            viewGroup: View,
            interpolator: Interpolator?,
            duration: Long,
            fromColor: Int = -1,
            toColor: Int = -1,
            toWidth: Int,
            toHeight: Int,
            fromCornerRadius: Float,
            toCornerRadius: Float,
            onStart: Action,
            onEnd: Action
        ) {

            val layoutParams = viewGroup.layoutParams as FrameLayout.LayoutParams

            val gradientDrawable = viewGroup.background as GradientDrawable

            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)

            val widthAnimation = ValueAnimator.ofInt(viewGroup.layoutParams.width, toWidth)
            val heightAnimation = ValueAnimator.ofInt(viewGroup.layoutParams.height, toHeight)

            val cornerAnimation = ObjectAnimator.ofFloat(
                gradientDrawable,
                CORNER_RADIUS_PROPERTY,
                fromCornerRadius,
                toCornerRadius
            )

            widthAnimation.interpolator = interpolator
            widthAnimation.duration = duration

            heightAnimation.interpolator = interpolator
            heightAnimation.duration = duration

            cornerAnimation.interpolator = interpolator
            cornerAnimation.duration = duration

            if (fromColor != toColor) {
                colorAnimation.addUpdateListener { animator ->
                    viewGroup.backgroundTintList = ColorStateList.valueOf(animator.animatedValue as Int)
                }
                colorAnimation.interpolator = interpolator
                colorAnimation.duration = duration
            }

            widthAnimation.addUpdateListener { valueAnimator ->
                layoutParams.width = valueAnimator.animatedValue as Int
                viewGroup.layoutParams = layoutParams
            }

            heightAnimation.addUpdateListener { valueAnimator ->
                layoutParams.height = valueAnimator.animatedValue as Int
                viewGroup.layoutParams = layoutParams
            }

            heightAnimation.addListener(MorphAnimationListener(onEnd = onEnd, onStart = onStart))

            widthAnimation.start()
            heightAnimation.start()
            cornerAnimation.start()

            if (fromColor != toColor)
                colorAnimation.start()
        }

        fun circularReveal(
            sourceView: View,
            resultView: View,
            interpolator: Interpolator,
            divider: Int = 2,
            duration: Long,
            onStart: Action,
            onEnd: Action
        ) {
            val startRadius = hypot(sourceView.width.toDouble(), sourceView.height.toDouble()).toFloat() / (divider * 2f)

            val location = IntArray(2)

            sourceView.getLocationOnScreen(location)

            val cx = location[0] + sourceView.width / 2
            val cy = location[1] + sourceView.height / 2

            circularReveal(startRadius, cx, cy, resultView, interpolator, duration, onStart, onEnd)
        }

        fun circularReveal(
            startRadius: Float,
            centerX: Int,
            centerY: Int,
            resultView: View,
            interpolator: Interpolator,
            duration: Long,
            onStart: Action,
            onEnd: Action
        ) {
            val endRadius = hypot(resultView.width.toDouble(), resultView.height.toDouble()).toFloat()

            val revealAnimator = ViewAnimationUtils.createCircularReveal(
                resultView,
                centerX,
                centerY,
                startRadius,
                endRadius
            )

            val listener = MorphAnimationListener(
                onStart = {
                    resultView.show()
                    onStart?.invoke()
                },
                onEnd = onEnd
            )

            revealAnimator.addListener(listener)
            revealAnimator.interpolator = interpolator
            revealAnimator.duration = duration
            revealAnimator.start()
        }

        fun circularConceal(
            sourceView: View,
            resultView: View,
            interpolator: Interpolator,
            duration: Long,
            onStart: Action,
            onEnd: Action
        ) {

            val endRadius: Float = hypot(resultView.width.toDouble(), resultView.height.toDouble()).toFloat() / 2f
            val startRadius: Float = hypot(sourceView.width.toDouble(), sourceView.height.toDouble()).toFloat()

            val location = IntArray(2)

            resultView.getLocationInWindow(location)

            val cx = location[0] + resultView.width / 2
            val cy = location[1] + resultView.height / 2

            val revealAnimator = ViewAnimationUtils.createCircularReveal(sourceView, cx, cy, startRadius, endRadius)

            revealAnimator.addListener(MorphAnimationListener(onStart, onEnd))
            revealAnimator.interpolator = interpolator
            revealAnimator.duration = duration
            revealAnimator.start()
        }

        fun rectangularReveal(
            sourceView: View,
            resultView: View,
            interpolator: Interpolator?,
            duration: Long,
            onStart: Action,
            onEnd: Action,
            startRadii: CornerRadii = CornerRadii(),
            endRadii: CornerRadii = CornerRadii()
        ) {

            val location = IntArray(2)

            val marginLayoutParams = sourceView.layoutParams as ViewGroup.MarginLayoutParams

            sourceView.getLocationOnScreen(location)

            val startDimension = Dimension(sourceView.width.toFloat(), sourceView.height.toFloat())

            val endDimension = Dimension(
                (resultView.width + (endRadii.topLeft + endRadii.bottomRight) / 6f),
                (resultView.height + (endRadii.topLeft + endRadii.bottomRight) / 6f)
            )

            val x = location[0]
            val y = location[1] - (sourceView.measuredHeight / 2 + marginLayoutParams.topMargin / 2)

            val toX = (-((endRadii.topLeft + endRadii.bottomRight) / 6)).toInt() / 2
            val toY = (-((endRadii.topLeft + endRadii.bottomRight) / 6)).toInt() / 2

            val revealAnimator = RectangularReveal.createRectangularReveal(
                resultView,
                x.toFloat(),
                y.toFloat(),
                toX.toFloat(),
                toY.toFloat(),
                startDimension,
                endDimension,
                startRadii,
                endRadii
            )

            revealAnimator.addListener(MorphAnimationListener(onStart, onEnd))

            if (interpolator != null) {
                revealAnimator.interpolator = interpolator
            }

            revealAnimator.duration = duration
            revealAnimator.start()
        }

        fun rectangularConceal(
            sourceView: View,
            resultView: View,
            interpolator: Interpolator?,
            duration: Long,
            onStart: Action,
            onEnd: Action,
            startRadii: CornerRadii = CornerRadii(),
            endRadii: CornerRadii = CornerRadii()
        ) {

            val location = IntArray(2)

            val marginLayoutParams = resultView.layoutParams as ViewGroup.MarginLayoutParams

            resultView.getLocationOnScreen(location)

            val startDimension = Dimension(resultView.width.toFloat(), resultView.height.toFloat())

            val endDimension = Dimension(
                (sourceView.width + (startRadii.topLeft + startRadii.bottomRight) / 6f),
                (sourceView.height + (startRadii.topLeft + startRadii.bottomRight) / 6f)
            )

            val x = location[0]
            val y = location[1] - (resultView.measuredHeight / 2 + marginLayoutParams.topMargin / 2)

            val toX = (-((startRadii.topLeft + startRadii.bottomRight) / 6)).toInt() / 2
            val toY = (-((startRadii.topLeft + startRadii.bottomRight) / 6)).toInt() / 2

            val revealAnimator = RectangularReveal.createRectangularReveal(
                sourceView,
                toX.toFloat(),
                toY.toFloat(),
                x.toFloat(),
                y.toFloat(),
                endDimension,
                startDimension,
                startRadii,
                endRadii
            )

            revealAnimator.addListener(MorphAnimationListener(onStart, onEnd))

            if (interpolator != null) {
                revealAnimator.interpolator = interpolator
            }
            revealAnimator.duration = duration
            revealAnimator.start()
        }

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
}
