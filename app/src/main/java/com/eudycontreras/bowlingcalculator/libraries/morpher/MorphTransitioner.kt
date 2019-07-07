package com.eudycontreras.bowlingcalculator.libraries.morpher

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.extensions.getProperties
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.libraries.morpher.utilities.ColorUtility
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import com.eudycontreras.bowlingcalculator.utilities.extensions.toStateList
import kotlin.math.roundToLong


class MorphTransitioner(
    private val startingView: MorphLayout,
    private val endingView: MorphLayout
) {

    private var remainingDuration: Long = 0L

    private var percentage = MIN_PERCENTAGE

    private var morphMaps: ArrayList<MorphMap> = arrayListOf()

    private var startingChildViews: ArrayList<View> = getAllChildren(startingView as View)
    private var endingChildViews: ArrayList<View> = getAllChildren(endingView as View)

    private var offsetListener: ((percent: Float) -> Unit)? = null

    private var revealChildrenOffset: Float = 0.75f

    private var durationOffsetMultiplier: Float = 0.25f

    private var revealChildren: Boolean = true
    private var initialValuesApplied = false

    var childrenRevealed: Boolean = false
        private set

    init {
        performSetup(true)
    }

    private fun performSetup(hideUntagged: Boolean) {
        if (hideUntagged) {
            endingChildViews.forEach {
                if (it.tag == null) {
                    it.visibility = View.INVISIBLE
                }
            }
        }
        for (startView in startingChildViews) {
            endingChildViews
                .filter { startView.tag == it.tag }
                .forEach { endView->
                    val start: MorphLayout = startView as MorphLayout
                    val end: MorphLayout = endView as MorphLayout

                    val startProps = start.getProperties()
                    val endProps = end.getProperties()

                    applyProps(end, startProps)

                    end.morphVisibility = View.VISIBLE
                    morphMaps.add(MorphMap(start, end, startProps, endProps))
                }
        }

        endingView.morphAlpha = 0f
        endingView.morphVisibility = View.VISIBLE
    }

    private fun getAllChildren(view: View): ArrayList<View> {

        val visited = ArrayList<View>()
        val unvisited = arrayListOf(view)

        while (unvisited.isNotEmpty()) {
            val child = unvisited.removeAt(0)
            visited.add(child)

            if (child !is ViewGroup)
                continue

            (0 until child.childCount).mapTo(unvisited) { child.getChildAt(it) }
        }
        return visited
    }

    private fun revealChildren(duration: Long) {
        childrenRevealed = true
        endingChildViews
            .filter { it.visibility == View.INVISIBLE}
            .forEach {
                it.visibility = View.VISIBLE
                it.translationY = -(16.dp)
                it.alpha = 0f
                it.animate()
                    .setStartDelay((duration * durationOffsetMultiplier).roundToLong())
                    .setDuration(duration + (duration * durationOffsetMultiplier).toLong())
                    .alpha(1f)
                    .translationY(0f)
                    .setInterpolator(null)
                    .start()
            }
    }

    private fun applyInitialValues() {
        endingView.morphAlpha = 1f
        initialValuesApplied = true
    }

    private fun applyProps(endView: MorphLayout, props: Properties) {
        endView.morphX = props.x
        endView.morphY = props.y
        endView.morphAlpha = props.alpha
        endView.morphElevation = props.elevation
        endView.morphTranslationZ = props.translationZ
        endView.morphPivotX = props.pivotX
        endView.morphPivotY = props.pivotY
        endView.morphRotation = props.rotation
        endView.morphRotationX = props.rotationX
        endView.morphRotationY = props.rotationY
        endView.morphScaleX = props.scaleX
        endView.morphScaleY = props.scaleY
        endView.morphStateList = props.stateList
        endView.morphWidth = props.width
        endView.morphHeight = props.height

        if (!endView.hasVectorDrawable()) {
            endView.updateCorners(props.cornerRadii)
        }

        endView.updateLayout()
    }

    private fun setOffset(offset: Float, view: View, startProps: Properties, endProps: Properties) {

    }

    private fun setOffset(offset: Float) {
        percentage = offset
        offsetListener?.invoke(offset)

        if (!initialValuesApplied) {
            applyInitialValues()
        }

        morphMaps.forEach {
            it.endView.morphX = (it.startProps.x + (it.endProps.x - it.startProps.x) * offset)
            it.endView.morphY = (it.startProps.y + (it.endProps.y - it.startProps.y) * offset)

            it.endView.morphAlpha = it.startProps.alpha + (it.endProps.alpha - it.startProps.alpha) * offset

            it.endView.morphScaleX = it.startProps.scaleX + (it.endProps.scaleX - it.startProps.scaleX) * offset
            it.endView.morphScaleY = it.startProps.scaleY + (it.endProps.scaleY - it.startProps.scaleY) * offset

            it.endView.morphPivotX = it.startProps.pivotX + (it.endProps.pivotX - it.startProps.pivotX) * offset
            it.endView.morphPivotY = it.startProps.pivotY + (it.endProps.pivotY - it.startProps.pivotY) * offset

            it.endView.morphRotation = it.startProps.rotation + (it.endProps.rotation - it.startProps.rotation) * offset

            it.endView.morphRotationX = it.startProps.rotationX + (it.endProps.rotationX - it.startProps.rotationX) * offset
            it.endView.morphRotationY = it.startProps.rotationY + (it.endProps.rotationY - it.startProps.rotationY) * offset

            it.endView.morphTranslationZ = it.startProps.translationZ + (it.endProps.translationZ - it.startProps.translationZ) * offset

            if (it.endView.showMutateCorners && !it.endView.hasVectorDrawable() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.endView.updateCorners(0, it.startProps.cornerRadii[0] + (it.endProps.cornerRadii[0] - it.startProps.cornerRadii[0]) * offset)
                it.endView.updateCorners(1, it.startProps.cornerRadii[1] + (it.endProps.cornerRadii[1] - it.startProps.cornerRadii[1]) * offset)
                it.endView.updateCorners(2, it.startProps.cornerRadii[2] + (it.endProps.cornerRadii[2] - it.startProps.cornerRadii[2]) * offset)
                it.endView.updateCorners(3, it.startProps.cornerRadii[3] + (it.endProps.cornerRadii[3] - it.startProps.cornerRadii[3]) * offset)
                it.endView.updateCorners(4, it.startProps.cornerRadii[4] + (it.endProps.cornerRadii[4] - it.startProps.cornerRadii[4]) * offset)
                it.endView.updateCorners(5, it.startProps.cornerRadii[5] + (it.endProps.cornerRadii[5] - it.startProps.cornerRadii[5]) * offset)
                it.endView.updateCorners(6, it.startProps.cornerRadii[6] + (it.endProps.cornerRadii[6] - it.startProps.cornerRadii[6]) * offset)
                it.endView.updateCorners(7, it.startProps.cornerRadii[7] + (it.endProps.cornerRadii[7] - it.startProps.cornerRadii[7]) * offset)
            }

            if (it.startView.morphTag.toString().contentEquals("createBowler")) {
                val d = 0
            }

            if (it.startProps.color != it.endProps.color) {
                it.endView.morphStateList = ColorUtility.interpolateColor(offset, it.startProps.color, it.endProps.color).toStateList()
            }

            it.endView.morphWidth = it.startProps.width + (it.endProps.width - it.startProps.width) * offset
            it.endView.morphHeight = it.startProps.height + (it.endProps.height - it.startProps.height) * offset

            it.endView.updateLayout()
        }
    }

    fun animateTo(
        percent: Float = MAX_PERCENTAGE,
        duration: Long = DEFAULT_DURATION,
        startDelay: Long = 0L,
        interpolator: TimeInterpolator? = null,
        trigger: OffsetTrigger? = null
    ) {
        this.remainingDuration = duration

        if (percentage == percent)
            return

        if (percent < MIN_PERCENTAGE || percent > MAX_PERCENTAGE)
            return

        ValueAnimator.ofFloat(percentage, percent).apply {
            this.duration = duration
            this.startDelay = startDelay
            this.interpolator = interpolator ?: DEFAULT_INTERPOLATOR

            addUpdateListener { animation ->

                val value = animation.animatedValue as Float

                remainingDuration = duration - (duration * value).toLong()

                setOffset(value)

                if (revealChildren && !childrenRevealed && value >= revealChildrenOffset) {
                    revealChildren(remainingDuration)
                }

                if (trigger != null && !trigger.hasTriggered) {
                    if (value >= trigger.percentage) {
                        trigger.action()
                        trigger.hasTriggered = true
                    }
                }
            }
            start()
        }
    }

    fun onOffsetChanged(progressListener: (percent: Float) -> Unit) {
        this.offsetListener = progressListener
    }

    fun setOffset(percent: Int) = setOffset(percent.toFloat() / 100f)

    companion object {
        const val MAX_PERCENTAGE: Float = 1f
        const val MIN_PERCENTAGE: Float = 0f

        const val DEFAULT_DURATION: Long = 300L

        val DEFAULT_INTERPOLATOR: TimeInterpolator = FastOutSlowInInterpolator()
    }

    private data class MorphMap(
        var startView: MorphLayout,
        var endView: MorphLayout,
        var startProps: Properties,
        var endProps: Properties
    )

    data class OffsetTrigger(
        val percentage: Float,
        val action: () -> Unit,
        var hasTriggered: Boolean = false
    )

    data class Properties(
        var x: Float,
        var y: Float,
        var width: Float,
        var height: Float,
        var alpha: Float,
        var elevation: Float,
        var translationZ: Float,
        var pivotX: Float,
        var pivotY: Float,
        var rotation: Float,
        var rotationX: Float,
        var rotationY: Float,
        var scaleX: Float,
        var scaleY: Float,
        var color: Int,
        var stateList: ColorStateList?,
        var cornerRadii: CornerRadii,
        var tag: String
    )
}
