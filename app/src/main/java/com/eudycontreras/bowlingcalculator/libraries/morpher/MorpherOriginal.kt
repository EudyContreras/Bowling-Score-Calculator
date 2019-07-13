/*
package com.eudycontreras.bowlingcalculator.libraries.morpher

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.extensions.getProperties
import com.eudycontreras.bowlingcalculator.libraries.morpher.extensions.toArrayList
import com.eudycontreras.bowlingcalculator.libraries.morpher.helpers.CurvedTranslationHelper
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.MorphAnimationListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Coordintates
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.libraries.morpher.utilities.ColorUtility
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import com.eudycontreras.bowlingcalculator.utilities.extensions.toStateList
import kotlin.math.abs
import kotlin.math.roundToLong


class MorpherOriginal {

    private var isSetup = false

    private lateinit var mappings: List<MorphMap>

    private lateinit var fromState: Properties
    private lateinit var toState: Properties

    private var offsetListener: OffsetListener = null

    private var remainingDuration: Long = 0L

    lateinit var startView: MorphLayout
    lateinit var endView: MorphLayout

    var morphIntoInterpolator: Interpolator? = null
    var morphFromInterpolator: Interpolator? = null

    var useArcTranslator = true

    var morphChildren = true

    var isMorphing = false
        private set

    var isMorphed = false
        private set

    var revealChildrenOffset: Float = DEFAULT_CHILDREN_REVEAL_OFFSET
        set(value) {
            if (value in 0f..1f) {
                field = value
            }
        }

    var concealChildrenOffset: Float = DEFAULT_CHILDREN_CONCEAL_OFFSET
        set(value) {
            if (value in 0f..1f) {
                field = value
            }
        }

    var childRevealOffsetMultiplier: Float = DEFAULT_REVEAL_DURATION_MULTIPLIER
    var childConcealOffsetMultiplier: Float = DEFAULT_CONCEAL_DURATION_MULTIPLIER

    var animateChildren: Boolean = true

    var deepChildSearch: Boolean = true

    var childrenRevealed: Boolean = false
        private set

    private fun setUp() {
        if (isSetup)
            return

        fromState = startView.getProperties()
        toState = endView.getProperties()

        mappings = if (morphChildren) {
            getChildMappings(startView, endView)
        } else emptyList()

        isSetup = true
    }

    fun morphInto(duration: Long, onStart: Action = null, onEnd: Action = null, offsetTrigger: OffsetTrigger? = null) {

        setUp()

        isMorphing = true

        for (child in endView.getChildren()) {
            if (child.visibility == View.GONE)
                continue

            if (morphChildren && child.tag != null)
                continue

            child.layoutParams.width = child.width
            child.layoutParams.height = child.height
            child.alpha = 0f
        }

        applyProps(endView, fromState)

        endView.morphVisibility = View.VISIBLE
        startView.morphVisibility = View.INVISIBLE

        val startX: Float = fromState.windowLocationX - (toState.width / 2f - fromState.width / 2f)
        val startY: Float = fromState.windowLocationY - (toState.height / 2f - fromState.height / 2f)

        val endX: Float = toState.windowLocationX.toFloat()
        val endY: Float = toState.windowLocationY.toFloat()

        val translationX: Float = abs(endX - startX)
        val translationY: Float = abs(endY - startY)

        fromState.translationX =  if (startX < endX) -translationX else translationX
        fromState.translationY =  if (startY < endY) -translationY else translationY

        endView.morphTranslationX = fromState.translationX
        endView.morphTranslationY = fromState.translationY

        val curveTranslator = CurvedTranslationHelper()
        curveTranslator.setStartPoint(fromState.getDeltaCoordinates())
        curveTranslator.setEndPoint(toState.getDeltaCoordinates())

        val midPoint = Coordintates.midPoint(fromState.getDeltaCoordinates(), toState.getDeltaCoordinates())
        val crossPoint = Coordintates(fromState.translationX, toState.translationY)

        curveTranslator.setControlPoint(Coordintates.midPoint(midPoint, crossPoint))

        morph(
            endView,
            fromState,
            toState,
            mappings,
            morphIntoInterpolator,
            curveTranslator,
            duration,
            onStart,
            onEnd,
            offsetTrigger,
            ChildrenAction.REVEAL,
            MorphType.INTO
        )
    }

    fun morphFrom(duration: Long, onStart: Action = null, onEnd: Action = null, offsetTrigger: OffsetTrigger? = null) {

        isMorphing = true

        val doOnEnd = {
            onEnd?.invoke()
            startView.morphVisibility = View.VISIBLE
            endView.morphVisibility = View.INVISIBLE
        }

        val curveTranslator = CurvedTranslationHelper()
        curveTranslator.setStartPoint(toState.getDeltaCoordinates())
        curveTranslator.setEndPoint(fromState.getDeltaCoordinates())

        val midPoint = Coordintates.midPoint(fromState.getDeltaCoordinates(), toState.getDeltaCoordinates())
        val crossPoint = Coordintates(fromState.translationX, toState.translationY)

        curveTranslator.setControlPoint(Coordintates.midPoint(midPoint, crossPoint))

        morph(
            endView,
            toState,
            fromState,
            mappings,
            morphFromInterpolator,
            curveTranslator,
            duration,
            onStart,
            doOnEnd,
            offsetTrigger,
            ChildrenAction.CONCEAL,
            MorphType.FROM
        )
    }

    private fun morph(
        endView: MorphLayout,
        startingProps: Properties,
        endingProps: Properties,
        mappings: List<MorphMap>,
        interpolator: Interpolator?,
        curveTranslator: CurvedTranslationHelper,
        duration: Long,
        onStart: Action,
        onEnd: Action,
        trigger: OffsetTrigger?,
        childrenAction: ChildrenAction,
        morphType: MorphType
    ) {

        this.remainingDuration = duration

        val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

        val listener = MorphAnimationListener(onStart) {
            onEnd?.invoke()
            isMorphing = false
            isMorphed = true
        }

        animator.addListener(listener)
        animator.addUpdateListener {
            val offset = it.animatedValue as Float

            remainingDuration = duration - (duration * offset).toLong()

            animateOffset(endView, startingProps, endingProps, offset)
            moveWithOffset(endView, startingProps, endingProps, offset, curveTranslator)

            if (morphChildren && mappings.isNotEmpty()) {
                for (mapping in mappings) {
                    when (morphType) {
                        MorphType.INTO -> {
                            animateOffset(mapping.endView, mapping.startProps, mapping.endProps, offset)

                            mapping.endView.morphX = (mapping.startProps.x + (mapping.endProps.x - mapping.startProps.x) * offset)
                            mapping.endView.morphY = (mapping.startProps.y + (mapping.endProps.y - mapping.startProps.y) * offset)
                        }
                        MorphType.FROM -> {
                            animateOffset(mapping.endView, mapping.endProps, mapping.startProps, offset)

                            mapping.endView.morphX = (mapping.endProps.x + (mapping.startProps.x - mapping.endProps.x) * offset)
                            mapping.endView.morphY = (mapping.endProps.y + (mapping.startProps.y - mapping.endProps.y) * offset)
                        }
                    }
                }
            }

            when (childrenAction) {
                ChildrenAction.CONCEAL -> {
                    if (animateChildren && childrenRevealed && offset >= concealChildrenOffset) {
                        concealChildren(morphChildren, remainingDuration)
                    }
                }
                ChildrenAction.REVEAL -> {
                    if (animateChildren && !childrenRevealed && offset >= revealChildrenOffset) {
                        revealChildren(morphChildren, remainingDuration)
                    }
                }
            }

            if (trigger != null && !trigger.hasTriggered) {
                if (offset >= trigger.percentage) {
                    trigger.triggerAction?.invoke()
                    trigger.hasTriggered = true
                }
            }
            offsetListener?.invoke(offset)
        }

        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }

    private fun moveWithOffset(
        endView: MorphLayout,
        startingProps: Properties,
        endingProps: Properties,
        offset: Float,
        curveTranslator: CurvedTranslationHelper
    ) {
        if (useArcTranslator) {
            endView.morphTranslationX = curveTranslator.getCurvedTranslationX(offset).toFloat()
            endView.morphTranslationY = curveTranslator.getCurvedTranslationY(offset).toFloat()
        } else {
            endView.morphTranslationX = (startingProps.translationX + (endingProps.translationX - startingProps.translationX) * offset)
            endView.morphTranslationY = (startingProps.translationY + (endingProps.translationY - startingProps.translationY) * offset)
        }
    }

    private fun getChildMappings(startView: MorphLayout, endView: MorphLayout): List<MorphMap> {
        val startChildren = getAllChildren(startView, deepChildSearch) { it.tag != null }
        val endChildren = getAllChildren(endView, deepChildSearch) { it.tag != null }

        val mappings: ArrayList<MorphMap> = ArrayList()

        startChildren.forEach { startChild ->
            endChildren.forEach { endChild ->
                if (startChild.tag == endChild.tag) {
                    val start: MorphLayout = startChild as MorphLayout
                    val end: MorphLayout = endChild as MorphLayout

                    val startProps = start.getProperties()
                    val endProps = end.getProperties()

                    mappings.add(MorphMap(start, end, startProps, endProps))
                }
            }
        }
        return mappings
    }

    private fun revealChildren(skipTagged: Boolean, duration: Long) {
        childrenRevealed = true
        val children = if (skipTagged) endView.getChildren().filter { it.tag == null } else endView.getChildren()
        animateRevealChildren(children, duration, childRevealOffsetMultiplier)
    }

    private fun concealChildren(skipTagged: Boolean, duration: Long) {
        childrenRevealed = false
        val children = if (skipTagged) endView.getChildren().filter { it.tag == null } else endView.getChildren()
        animateConcealChildren(children, duration, childConcealOffsetMultiplier)
    }

    enum class MorphType {
        INTO, FROM
    }


    enum class ChildrenAction { REVEAL, CONCEAL }

    companion object {

        const val MAX_PERCENTAGE: Float = 1f
        const val MIN_PERCENTAGE: Float = 0f

        const val DEFAULT_DURATION: Long = 300L

        const val DEFAULT_CHILDREN_REVEAL_OFFSET = 0.60f
        const val DEFAULT_CHILDREN_CONCEAL_OFFSET = 0.0f

        const val DEFAULT_REVEAL_DURATION_MULTIPLIER = 0.2f
        const val DEFAULT_CONCEAL_DURATION_MULTIPLIER = 0.4f

        private fun applyProps(view: MorphLayout, props: Properties) {
            view.morphX = props.x
            view.morphY = props.y
            view.morphAlpha = props.alpha
            view.morphElevation = props.elevation
            view.morphTranslationX = props.translationX
            view.morphTranslationY = props.translationY
            view.morphTranslationZ = props.translationZ
            view.morphPivotX = props.pivotX
            view.morphPivotY = props.pivotY
            view.morphRotation = props.rotation
            view.morphRotationX = props.rotationX
            view.morphRotationY = props.rotationY
            view.morphScaleX = props.scaleX
            view.morphScaleY = props.scaleY
            view.morphStateList = props.stateList
            view.morphWidth = props.width
            view.morphHeight = props.height

            if (!view.hasVectorDrawable()) {
                view.updateCorners(props.cornerRadii)
            }

            view.updateLayout()
        }

        private fun animateOffset(
            morphView: MorphLayout,
            startingProps: Properties,
            endingProps: Properties,
            offset: Float
        ) {
            if (endingProps.alpha != startingProps.alpha) {
                morphView.morphAlpha = startingProps.alpha + (endingProps.alpha - startingProps.alpha) * offset
            }

            if (endingProps.scaleX != startingProps.scaleX || endingProps.scaleY != startingProps.scaleY) {
                morphView.morphScaleX = startingProps.scaleX + (endingProps.scaleX - startingProps.scaleX) * offset
                morphView.morphScaleY = startingProps.scaleY + (endingProps.scaleY - startingProps.scaleY) * offset
            }

            if (endingProps.pivotX != startingProps.pivotX || endingProps.pivotY != startingProps.pivotY) {
                morphView.morphPivotX = startingProps.pivotX + (endingProps.pivotX - startingProps.pivotX) * offset
                morphView.morphPivotY = startingProps.pivotY + (endingProps.pivotY - startingProps.pivotY) * offset
            }

            if (endingProps.rotation != startingProps.rotation) {
                morphView.morphRotation = startingProps.rotation + (endingProps.rotation - startingProps.rotation) * offset
            }

            if (endingProps.rotationX != startingProps.rotationX || endingProps.rotationY != startingProps.rotationY) {
                morphView.morphRotationX = startingProps.rotationX + (endingProps.rotationX - startingProps.rotationX) * offset
                morphView.morphRotationY = startingProps.rotationY + (endingProps.rotationY - startingProps.rotationY) * offset
            }

            if (endingProps.translationZ != startingProps.translationZ) {
                morphView.morphTranslationZ = startingProps.translationZ + (endingProps.translationZ - startingProps.translationZ) * offset
            }

            if (morphView.mutateCorners && !morphView.hasVectorDrawable() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                morphView.updateCorners(0, startingProps.cornerRadii[0] + (endingProps.cornerRadii[0] - startingProps.cornerRadii[0]) * offset)
                morphView.updateCorners(1, startingProps.cornerRadii[1] + (endingProps.cornerRadii[1] - startingProps.cornerRadii[1]) * offset)
                morphView.updateCorners(2, startingProps.cornerRadii[2] + (endingProps.cornerRadii[2] - startingProps.cornerRadii[2]) * offset)
                morphView.updateCorners(3, startingProps.cornerRadii[3] + (endingProps.cornerRadii[3] - startingProps.cornerRadii[3]) * offset)
                morphView.updateCorners(4, startingProps.cornerRadii[4] + (endingProps.cornerRadii[4] - startingProps.cornerRadii[4]) * offset)
                morphView.updateCorners(5, startingProps.cornerRadii[5] + (endingProps.cornerRadii[5] - startingProps.cornerRadii[5]) * offset)
                morphView.updateCorners(6, startingProps.cornerRadii[6] + (endingProps.cornerRadii[6] - startingProps.cornerRadii[6]) * offset)
                morphView.updateCorners(7, startingProps.cornerRadii[7] + (endingProps.cornerRadii[7] - startingProps.cornerRadii[7]) * offset)
            }

            if (startingProps.color != endingProps.color) {
                morphView.morphStateList = ColorUtility.interpolateColor(offset, startingProps.color, endingProps.color).toStateList()
            }

            if (endingProps.width != startingProps.width || endingProps.height != startingProps.height) {
                morphView.morphWidth = startingProps.width + (endingProps.width - startingProps.width) * offset
                morphView.morphHeight = startingProps.height + (endingProps.height - startingProps.height) * offset

                morphView.updateLayout()
            }
        }

        private fun getAllChildren(view: MorphLayout, deepSearch: Boolean, predicate: ((View) -> Boolean)? = null): ArrayList<View> {

            if (!deepSearch) {
                return if (predicate != null) {
                    view.getChildren().filter(predicate).toArrayList()
                } else {
                    view.getChildren().toArrayList()
                }
            }

            val visited = ArrayList<View>()
            val unvisited = ArrayList<View>()

            var initialCondition = true

            while (unvisited.isNotEmpty() || initialCondition) {

                if (!initialCondition) {
                    val child = unvisited.removeAt(0)

                    if (predicate != null) {
                        if (predicate(child)) {
                            visited.add(child)
                        }
                    } else {
                        visited.add(child)
                    }


                    if (child !is ViewGroup)
                        continue

                    (0 until child.childCount).mapTo(unvisited) { child.getChildAt(it) }
                } else {
                    (0 until view.morphChildCount).mapTo(unvisited) { view.getChildViewAt(it) }

                    initialCondition = false
                }
            }

            return if (predicate != null) {
                visited.filter(predicate).toArrayList()
            } else {
                visited.toArrayList()
            }
        }

        private fun <T: View> animateRevealChildren(children: Sequence<T>, duration: Long, durationOffsetMultiplier: Float) {
            children.forEach {
                it.visibility = View.VISIBLE
                it.translationY = (12.dp)
                it.scaleX = 0.8f
                it.scaleY = 0.8f
                it.alpha = 0f
                it.animate()
                    .setListener(null)
                    .setDuration((duration + (duration * durationOffsetMultiplier)).roundToLong())
                    .setStartDelay(0)
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0f)
                    .setInterpolator(LinearOutSlowInInterpolator())
                    .start()
            }
        }

        private fun <T: View> animateConcealChildren(children: Sequence<T>, duration: Long, durationOffsetMultiplier: Float) {
            children.forEach {
                it.visibility = View.VISIBLE
                it.translationY = 0f
                it.alpha = 1f
                it.animate()
                    .setListener(null)
                    .setDuration((duration * durationOffsetMultiplier).roundToLong())
                    .setStartDelay(0)
                    .alpha(0f)
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .translationY((-8).dp)
                    .setInterpolator(AccelerateInterpolator())
                    .start()
            }
        }
    }

    private data class MorphMap(
        var startView: MorphLayout,
        var endView: MorphLayout,
        var startProps: Properties,
        var endProps: Properties
    )

    data class OffsetTrigger(
        val percentage: Float,
        val triggerAction: Action,
        var hasTriggered: Boolean = false
    )

    data class Properties(
        var x: Float,
        var y: Float,
        var width: Float,
        var height: Float,
        var alpha: Float,
        var elevation: Float,
        var translationX: Float,
        var translationY: Float,
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
        var windowLocationX: Int,
        var windowLocationY: Int,
        var tag: String
    ) {
        fun getDeltaCoordinates() = Coordintates(translationX, translationY)

        fun getCoordinates() = Coordintates(x, y)
    }
}
*/
