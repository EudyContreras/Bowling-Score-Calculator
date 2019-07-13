/*
package com.eudycontreras.bowlingcalculator.libraries.morpher

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.opengl.Visibility
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
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


class MorphTransitionerAlt {

    private var remainingDuration: Long = 0L

    private var percentage: Float = MIN_OFFSET

    private var morphMaps: ArrayList<MorphMap> = ArrayList()

    private var offsetListener: OffsetListener = null
    private var onTransitionEnd: Action = null

    private var initialValuesApplied: Boolean = false

    var durationOffsetMultiplier: Float = DEFAULT_REVEAL_DURATION_MULTIPLIER

    var animateRevealChildren: Boolean = true

    var childrenRevealed: Boolean = false
        private set

    var childRevealOffset: Float = DEFAULT_CHILDREN_REVEAL_OFFSET
        set(value) {
            if (value in 0f..1f) {
                field = value
            }
        }

    private fun performSetup(startingView: MorphLayout, endingView: MorphLayout, hideUntagged: Boolean): ViewMappingData {
        val startingChildViews = getAllChildren(startingView, true)
        val endingChildViews =  getAllChildren(endingView, true)

        startingChildViews.add(0, startingView as View)
        endingChildViews.add(0, endingView as View)

        if (hideUntagged) {
            endingChildViews.forEach {
                if (it.tag == null) {
                    it.visibility = View.INVISIBLE
                }
            }
        }

        for (startView in startingChildViews) {
            if (startView.tag == null)
                continue

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

        return ViewMappingData(startingChildViews, endingChildViews)
    }

    private fun applyInitialValues(endView: MorphLayout) {
        endView.morphAlpha = 1f
        initialValuesApplied = true
    }

    private fun setOffset(offset: Float) {
        percentage = offset

        morphMaps.forEach {
            it.endView.morphX = it.startProps.x + (it.endProps.x - it.startProps.x) * offset
            it.endView.morphY = it.startProps.y + (it.endProps.y - it.startProps.y) * offset

            animateProperties(it.endView, it.startProps, it.endProps, offset)
        }

        offsetListener?.invoke(offset)
    }

    fun animateTo(
        fromView: MorphLayout,
        toView: MorphLayout,
        percent: Float = MAX_OFFSET,
        duration: Long = DEFAULT_DURATION,
        startDelay: Long = 0L,
        interpolator: TimeInterpolator? = DecelerateInterpolator(),
        trigger: OffsetTrigger? = null
    ) {
        this.remainingDuration = duration

        if (percentage == percent)
            return

        if (percent < MIN_OFFSET || percent > MAX_OFFSET)
            return

        val data = performSetup(fromView, toView,true)

        val animator: ValueAnimator = ValueAnimator.ofFloat(percentage, percent)

        val listener = MorphAnimationListener({ applyInitialValues(toView) },onTransitionEnd)

        animator.addListener(listener)
        animator.addUpdateListener {
            val fraction = it.animatedValue as Float

            remainingDuration = duration - (duration * fraction).toLong()

            setOffset(fraction)

            if (animateRevealChildren && !childrenRevealed && fraction >= childRevealOffset) {
                val children = data.toViewChildren.filter { child -> child.visibility == View.INVISIBLE}
                animateRevealChildren(children.asSequence(), duration, durationOffsetMultiplier) { childrenRevealed = true }
            }

            if (trigger != null && !trigger.hasTriggered) {
                if (fraction >= trigger.percentage) {
                    trigger.triggerAction?.invoke()
                    trigger.hasTriggered = true
                }
            }
        }
        animator.duration = duration
        animator.startDelay = startDelay
        animator.interpolator = interpolator
        animator.start()
    }

    fun doWhenTransitionEnds(action: Action) {
        this.onTransitionEnd = action
    }

    fun onOffsetChanged(progressListener: (percent: Float) -> Unit) {
        this.offsetListener = progressListener
    }

    fun setOffset(percent: Int) = setOffset(percent.toFloat() / 100f)

    private data class ViewMappingData(
        val fromViewChildren: ArrayList<View>,
        val toViewChildren:  ArrayList<View>
    )

    class Morpher {
        private val curveTranslator = CurvedTranslationHelper()

        private lateinit var startingView: MorphLayout
        private lateinit var endingView: MorphLayout

        private lateinit var startingState: Properties
        private lateinit var endingState: Properties

        private lateinit var mappings: List<MorphMap>

        private var offsetListener: OffsetListener = null

        private var remainingDuration: Long = 0L
        private var mappingsCreated = false

        var morphIntoInterpolator: Interpolator? = null
        var morphFromInterpolator: Interpolator? = null

        var childRevealOffsetMultiplier: Float = DEFAULT_REVEAL_DURATION_MULTIPLIER
        var childConcealOffsetMultiplier: Float = DEFAULT_CONCEAL_DURATION_MULTIPLIER

        var childTransitionProperties: ChildTransitionProperties = ChildTransitionProperties()
            private set

        var useArcTranslator = true
        var morphChildren = true

        var animateChildren: Boolean = true
        var useDeepChildSearch: Boolean = true

        var childrenRevealed: Boolean = false
            private set

        var isMorphing: Boolean = false
            private set

        var isMorphed: Boolean = false
            private set

        var childRevealOffset: Float = DEFAULT_CHILDREN_REVEAL_OFFSET
            set(value) {
                if (value in 0f..1f) {
                    field = value
                }
            }

        var childConcealOffset: Float = DEFAULT_CHILDREN_CONCEAL_OFFSET
            set(value) {
                if (value in 0f..1f) {
                    field = value
                }
            }

        var startView: MorphLayout
            get() = startingView
            set(value) {
                startingView = value
                mappingsCreated = false
            }

        var endView: MorphLayout
            get() = endingView
            set(value) {
                endingView = value
                mappingsCreated = false
            }

        private fun createMappings() {
            startingState = startingView.getProperties()
            endingState = endingView.getProperties()

            mappings = if (morphChildren) {
                getChildMappings(startingView, endingView)
            } else emptyList()
        }

        fun morphInto(duration: Long, onStart: Action = null, onEnd: Action = null, offsetTrigger: OffsetTrigger? = null) {

            if (!mappingsCreated) {
                createMappings()
                mappingsCreated = true
            }

            isMorphing = true

            for (child in endingView.getChildren()) {
                if (child.visibility == View.GONE)
                    continue

                if (morphChildren && child.tag != null)
                    continue

                child.alpha = 0f
            }

            applyProps(endingView, startingState)

            endingView.morphVisibility = View.VISIBLE
            startingView.morphVisibility = View.INVISIBLE

            val startX: Float = startingState.windowLocationX - (endingState.width / 2f - startingState.width / 2f)
            val startY: Float = startingState.windowLocationY - (endingState.height / 2f - startingState.height / 2f)

            val endX: Float = endingState.windowLocationX.toFloat()
            val endY: Float = endingState.windowLocationY.toFloat()

            val translationX: Float = abs(endX - startX)
            val translationY: Float = abs(endY - startY)

            startingState.translationX =  if (startX < endX) -translationX else translationX
            startingState.translationY =  if (startY < endY) -translationY else translationY

            endingView.morphTranslationX = startingState.translationX
            endingView.morphTranslationY = startingState.translationY

            curveTranslator.setStartPoint(startingState.getDeltaCoordinates())
            curveTranslator.setEndPoint(endingState.getDeltaCoordinates())

            curveTranslator.setControlPoint(Coordintates(endingState.translationX, startingState.translationY))

            morph(
                endingView,
                startingState,
                endingState,
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

            if (!mappingsCreated) {
                createMappings()
                mappingsCreated = true
            }

            isMorphing = true

            val doOnEnd = {
                onEnd?.invoke()
                startingView.morphVisibility = View.VISIBLE
                endingView.morphVisibility = View.INVISIBLE

                applyProps(endingView, endingState)

                mappings.forEach {
                    applyProps(it.endView, it.endProps)
                }
            }

            curveTranslator.setStartPoint(endingState.getDeltaCoordinates())
            curveTranslator.setEndPoint(startingState.getDeltaCoordinates())

            curveTranslator.setControlPoint(Coordintates(endingState.translationX, startingState.translationY))

            morph(
                endingView,
                endingState,
                startingState,
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
            curveTranslationHelper: CurvedTranslationHelper?,
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
                val fraction = it.animatedValue as Float

                remainingDuration = duration - (duration * fraction).toLong()

                animateProperties(endView, startingProps, endingProps, fraction)
                moveWithOffset(endView, startingProps, endingProps, fraction, curveTranslationHelper)

                if (morphChildren && mappings.isNotEmpty()) {
                    for (mapping in mappings) {
                        when (morphType) {
                            MorphType.INTO -> {
                                animateProperties(mapping.endView, mapping.startProps, mapping.endProps, fraction)

                                mapping.endView.morphX = mapping.startProps.x + (mapping.endProps.x - mapping.startProps.x) * fraction
                                mapping.endView.morphY = mapping.startProps.y + (mapping.endProps.y - mapping.startProps.y) * fraction
                            }
                            MorphType.FROM -> {
                                animateProperties(mapping.endView, mapping.endProps, mapping.startProps, fraction)

                                mapping.endView.morphX = mapping.endProps.x + (mapping.startProps.x - mapping.endProps.x) * fraction
                                mapping.endView.morphY = mapping.endProps.y + (mapping.startProps.y - mapping.endProps.y) * fraction
                            }
                        }
                    }
                }

                when (childrenAction) {
                    ChildrenAction.CONCEAL -> {
                        if (animateChildren && childrenRevealed && fraction >= childConcealOffset) {
                            concealChildren(morphChildren, remainingDuration)
                        }
                    }
                    ChildrenAction.REVEAL -> {
                        if (animateChildren && !childrenRevealed && fraction >= childRevealOffset) {
                            revealChildren(morphChildren, remainingDuration)
                        }
                    }
                }

                if (trigger != null && !trigger.hasTriggered) {
                    if (fraction >= trigger.percentage) {
                        trigger.triggerAction?.invoke()
                        trigger.hasTriggered = true
                    }
                }
                offsetListener?.invoke(fraction)
            }

            animator.interpolator = interpolator
            animator.duration = duration
            animator.start()
        }

        private fun moveWithOffset(
            endView: MorphLayout,
            startingProps: Properties,
            endingProps: Properties,
            fraction: Float,
            curveTranslationHelper: CurvedTranslationHelper?
        ) {
            if (useArcTranslator && curveTranslationHelper != null) {
                endView.morphTranslationX = curveTranslationHelper.getCurvedTranslationX(fraction).toFloat()
                endView.morphTranslationY = curveTranslationHelper.getCurvedTranslationY(fraction).toFloat()
            } else {
                endView.morphTranslationX = startingProps.translationX + (endingProps.translationX - startingProps.translationX) * fraction
                endView.morphTranslationY = startingProps.translationY + (endingProps.translationY - startingProps.translationY) * fraction
            }
        }

        private fun getChildMappings(startView: MorphLayout, endView: MorphLayout): List<MorphMap> {
            val startChildren = getAllChildren(startView, useDeepChildSearch) { it.tag != null }
            val endChildren = getAllChildren(endView, useDeepChildSearch) { it.tag != null }

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
            val children = if (skipTagged) endingView.getChildren().filter { it.tag == null } else endingView.getChildren()
            animateRevealChildren(children, childTransitionProperties, duration, childRevealOffsetMultiplier) { childrenRevealed = true }
        }

        private fun concealChildren(skipTagged: Boolean, duration: Long) {
            val children = if (skipTagged) endingView.getChildren().filter { it.tag == null } else endingView.getChildren()
            animateConcealChildren(children, childTransitionProperties, duration, childConcealOffsetMultiplier) { childrenRevealed = false }
        }
    }


    companion object {

        const val MAX_OFFSET: Float = 1f
        const val MIN_OFFSET: Float = 0f

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

            if (!view.hasVectorDrawable() && view.mutateCorners) {
                view.updateCorners(0, props.cornerRadii[0])
                view.updateCorners(1, props.cornerRadii[1])
                view.updateCorners(2, props.cornerRadii[2])
                view.updateCorners(3, props.cornerRadii[3])
                view.updateCorners(4, props.cornerRadii[4])
                view.updateCorners(5, props.cornerRadii[5])
                view.updateCorners(6, props.cornerRadii[6])
                view.updateCorners(7, props.cornerRadii[7])
            }

            view.updateLayout()
        }

        private fun animateProperties(
            morphView: MorphLayout,
            startingProps: Properties,
            endingProps: Properties,
            fraction: Float
        ) {
            if (endingProps.alpha != startingProps.alpha) {
                morphView.morphAlpha = startingProps.alpha + (endingProps.alpha - startingProps.alpha) * fraction
            }

            if (endingProps.scaleX != startingProps.scaleX || endingProps.scaleY != startingProps.scaleY) {
                morphView.morphScaleX = startingProps.scaleX + (endingProps.scaleX - startingProps.scaleX) * fraction
                morphView.morphScaleY = startingProps.scaleY + (endingProps.scaleY - startingProps.scaleY) * fraction
            }

            if (endingProps.pivotX != startingProps.pivotX || endingProps.pivotY != startingProps.pivotY) {
                morphView.morphPivotX = startingProps.pivotX + (endingProps.pivotX - startingProps.pivotX) * fraction
                morphView.morphPivotY = startingProps.pivotY + (endingProps.pivotY - startingProps.pivotY) * fraction
            }

            if (endingProps.rotation != startingProps.rotation) {
                morphView.morphRotation = startingProps.rotation + (endingProps.rotation - startingProps.rotation) * fraction
            }

            if (endingProps.rotationX != startingProps.rotationX || endingProps.rotationY != startingProps.rotationY) {
                morphView.morphRotationX = startingProps.rotationX + (endingProps.rotationX - startingProps.rotationX) * fraction
                morphView.morphRotationY = startingProps.rotationY + (endingProps.rotationY - startingProps.rotationY) * fraction
            }

            if (endingProps.translationZ != startingProps.translationZ) {
                morphView.morphTranslationZ = startingProps.translationZ + (endingProps.translationZ - startingProps.translationZ) * fraction
            }

            if (endingProps.elevation != startingProps.elevation) {
                morphView.morphElevation = startingProps.elevation + (endingProps.elevation - startingProps.elevation) * fraction
            }

            if (morphView.mutateCorners && !morphView.hasVectorDrawable() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                morphView.updateCorners(0, startingProps.cornerRadii[0] + (endingProps.cornerRadii[0] - startingProps.cornerRadii[0]) * fraction)
                morphView.updateCorners(1, startingProps.cornerRadii[1] + (endingProps.cornerRadii[1] - startingProps.cornerRadii[1]) * fraction)
                morphView.updateCorners(2, startingProps.cornerRadii[2] + (endingProps.cornerRadii[2] - startingProps.cornerRadii[2]) * fraction)
                morphView.updateCorners(3, startingProps.cornerRadii[3] + (endingProps.cornerRadii[3] - startingProps.cornerRadii[3]) * fraction)
                morphView.updateCorners(4, startingProps.cornerRadii[4] + (endingProps.cornerRadii[4] - startingProps.cornerRadii[4]) * fraction)
                morphView.updateCorners(5, startingProps.cornerRadii[5] + (endingProps.cornerRadii[5] - startingProps.cornerRadii[5]) * fraction)
                morphView.updateCorners(6, startingProps.cornerRadii[6] + (endingProps.cornerRadii[6] - startingProps.cornerRadii[6]) * fraction)
                morphView.updateCorners(7, startingProps.cornerRadii[7] + (endingProps.cornerRadii[7] - startingProps.cornerRadii[7]) * fraction)
            }

            if (startingProps.color != endingProps.color) {
                morphView.morphStateList = ColorUtility.interpolateColor(fraction, startingProps.color, endingProps.color).toStateList()
            }

            if (endingProps.width != startingProps.width || endingProps.height != startingProps.height) {
                morphView.morphWidth = startingProps.width + (endingProps.width - startingProps.width) * fraction
                morphView.morphHeight = startingProps.height + (endingProps.height - startingProps.height) * fraction

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

            return visited
        }

        private fun <T: View> animateRevealChildren(
            children: Sequence<T>,
            transitionProps: ChildTransitionProperties,
            duration: Long,
            durationOffsetMultiplier: Float,
            onEnd: Action = null
        ) {
            children.forEach {
                it.visibility = transitionProps.revealProperties.visibility
                it.translationY = transitionProps.revealProperties.translationY
                it.scaleX = transitionProps.revealProperties.scaleX
                it.scaleY = transitionProps.revealProperties.scaleY
                it.alpha = transitionProps.revealProperties.alpha
                it.animate()
                    .withEndAction(onEnd)
                    .setDuration((duration + (duration * durationOffsetMultiplier)).roundToLong())
                    .setStartDelay(0)
                    .alpha(transitionProps.concealProperties.alpha)
                    .scaleX(transitionProps.concealProperties.scaleX)
                    .scaleY(transitionProps.concealProperties.scaleY)
                    .translationY(transitionProps.concealProperties.translationY)
                    .setInterpolator(LinearOutSlowInInterpolator())
                    .start()
            }
        }

        private fun <T: View> animateConcealChildren(
            children: Sequence<T>,
            transitionProps: ChildTransitionProperties,
            duration: Long,
            durationOffsetMultiplier: Float,
            onEnd: Action = null
        ) {
            children.forEach {
                it.visibility = transitionProps.concealProperties.visibility
                it.translationY = transitionProps.concealProperties.translationY
                it.alpha = transitionProps.concealProperties.alpha
                it.animate()
                    .withEndAction(onEnd)
                    .setDuration((duration * durationOffsetMultiplier).roundToLong())
                    .setStartDelay(0)
                    .alpha(transitionProps.revealProperties.alpha)
                    .scaleX(transitionProps.revealProperties.scaleX)
                    .scaleY(transitionProps.revealProperties.scaleY)
                    .translationY(-transitionProps.revealProperties.translationY)
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
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val alpha: Float,
        val elevation: Float,
        var translationX: Float,
        var translationY: Float,
        val translationZ: Float,
        val pivotX: Float,
        val pivotY: Float,
        val rotation: Float,
        val rotationX: Float,
        val rotationY: Float,
        val scaleX: Float,
        val scaleY: Float,
        val color: Int,
        val stateList: ColorStateList?,
        val cornerRadii: CornerRadii,
        val windowLocationX: Int,
        val windowLocationY: Int,
        val tag: String
    ) {
        fun getDeltaCoordinates() = Coordintates(translationX, translationY)

        fun getCoordinates() = Coordintates(x, y)
    }

    data class ChildProperties(
        var alpha: Float = 1f,
        var elevation: Float = 0f,
        var translationX: Float = 0f,
        var translationY: Float = 0f,
        var translationZ: Float = 0f,
        var rotation: Float = 0f,
        var rotationX: Float = 0f,
        var rotationY: Float = 0f,
        var scaleX: Float = 0f,
        var scaleY: Float = 0f,
        var visibility: Int = View.VISIBLE
    )

    data class ChildTransitionProperties(
        val revealProperties: ChildProperties = ChildProperties(
            alpha = 0f,
            scaleX = 0.8f,
            scaleY = 0.8f,
            translationY = 12.dp,
            visibility = View.VISIBLE
        ),
        val concealProperties: ChildProperties = ChildProperties()
    )

    enum class MorphType { INTO, FROM }

    enum class ChildrenAction { REVEAL, CONCEAL }
}
*/