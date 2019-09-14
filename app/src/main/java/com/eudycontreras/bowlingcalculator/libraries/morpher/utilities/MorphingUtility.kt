package com.eudycontreras.bowlingcalculator.libraries.morpher.utilities

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.graphics.drawable.VectorDrawable
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.Interpolator
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.eudycontreras.bowlingcalculator.libraries.morpher.efffectShapes.RectangularReveal
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.MorphAnimationListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Dimension
import com.eudycontreras.bowlingcalculator.utilities.Action
import com.eudycontreras.bowlingcalculator.utilities.extensions.show
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
object MorphingUtility {

    private const val CORNER_RADIUS_PROPERTY = "cornerRadius"

    private const val SCALE_X_PROPERTY = "scaleX"
    private const val SCALE_Y_PROPERTY = "scaleY"

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
            {
                resultView.show()
                onStart?.invoke()
            },
            onEnd
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

    fun getBitmapFromDrawable(context: Context, @DrawableRes drawableId: Int): Bitmap {
        val drawable = AppCompatResources.getDrawable(context, drawableId)
        return getBitmapFromDrawable(drawable)
    }

    fun getBitmapFromDrawable(drawable: Drawable?): Bitmap {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is VectorDrawableCompat || drawable is VectorDrawable) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            bitmap
        } else if (drawable is TransitionDrawable){
            val bitmap = Bitmap.createBitmap(drawable.getDrawable(0).intrinsicWidth, drawable.getDrawable(0).intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            bitmap
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }
}
