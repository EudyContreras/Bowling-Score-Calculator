package com.eudycontreras.bowlingcalculator.libraries.morpher

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.eudycontreras.bowlingcalculator.libraries.morpher.drawables.MorphTransitionDrawable
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.extensions.*
import com.eudycontreras.bowlingcalculator.libraries.morpher.helpers.CurvedTranslationHelper
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.MorphAnimationListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Coordintates
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.libraries.morpher.utilities.ColorUtility
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import com.eudycontreras.bowlingcalculator.utilities.extensions.toStateList
import kotlin.math.abs
import kotlin.math.roundToLong


class Morpher(private val context: Context) {

    private var remainingDuration: Long = 0L

    private var mappingsCreated: Boolean = false
    private var initialValuesApplied: Boolean = false

    private val curveTranslator = CurvedTranslationHelper()

    private var transitionOffsetListener: TransitionOffsetListener = null

    private lateinit var startingView: MorphLayout
    private lateinit var endingView: MorphLayout

    lateinit var startingState: Properties
    lateinit var endingState: Properties

    private lateinit var mappings: List<MorphMap>

    private var children: List<View>? = null

    var morphIntoInterpolator: Interpolator? = null
    var morphFromInterpolator: Interpolator? = null

    var useDeepChildSearch: Boolean = true
    var useArcTranslator: Boolean = true
    var animateChildren: Boolean = true
    var morphChildren: Boolean = true

    var childrenRevealed: Boolean = false
        private set
    var isMorphing: Boolean = false
        private set
    var isMorphed: Boolean = false
        private set

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

    var endStateMorphIntoDescriptor: AnimationDescriptor = AnimationDescriptor(
        type =  AnimationType.REVEAL,
        animateOnOffset = DEFAULT_CHILDREN_REVEAL_OFFSET,
        durationMultiplier = DEFAULT_REVEAL_DURATION_MULTIPLIER,
        startStateProps = AnimationProperties.defaultInPropsStart(),
        endStateProps = AnimationProperties.defaultInPropsEnd(),
        interpolator = FastOutSlowInInterpolator(),
        duration = 400,
        stagger = AnimationStagger()
    )

    var endStateMorphFromDescriptor: AnimationDescriptor = AnimationDescriptor(
        type =  AnimationType.CONCEAL,
        animateOnOffset = DEFAULT_CHILDREN_CONCEAL_OFFSET,
        durationMultiplier = DEFAULT_CONCEAL_DURATION_MULTIPLIER,
        startStateProps = AnimationProperties.defaultOutPropsStart(),
        endStateProps = AnimationProperties.defaultOutPropsEnd(),
        interpolator = DecelerateInterpolator()
    )

    var startStateMorphIntoDescriptor: AnimationDescriptor = AnimationDescriptor(
        type =  AnimationType.REVEAL,
        animateOnOffset = DEFAULT_CHILDREN_REVEAL_OFFSET,
        durationMultiplier = DEFAULT_REVEAL_DURATION_MULTIPLIER,
        startStateProps = AnimationProperties(),
        endStateProps = AnimationProperties(),
        interpolator = LinearOutSlowInInterpolator()
    )

    var startStateMorphFromDescriptor: AnimationDescriptor = AnimationDescriptor(
        type =  AnimationType.CONCEAL,
        animateOnOffset = DEFAULT_CHILDREN_CONCEAL_OFFSET,
        durationMultiplier = DEFAULT_CONCEAL_DURATION_MULTIPLIER,
        startStateProps = AnimationProperties(),
        endStateProps = AnimationProperties(),
        interpolator = DecelerateInterpolator()
    )

    private fun createMappings() {
        startingState = startingView.getProperties()
        endingState = endingView.getProperties()

        mappings = if (morphChildren) {
            getChildMappings(startingView, endingView)
        } else emptyList()
    }

    private fun performSetup() {
        if (!mappingsCreated) {
            createMappings()
            mappingsCreated = true
        }

        val endChildren = getAllChildren(endingView, useDeepChildSearch) { it.tag != null }

        endChildren.forEach {
            val endView = it as MorphLayout
            if (endView.hasMorphTransitionDrawable()) {
                val transition = endView.getMorphTransitionDrawable()
                transition.resetTransition()
            }
        }

        for (child in endingView.getChildren()) {
            if (child.visibility == View.GONE)
                continue

            if (morphChildren && child.tag != null)
                continue

            child.alpha = 0f
            child.layoutParams.width = child.width
            child.layoutParams.height = child.height
        }

        applyProps(endingView, startingState)

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

        initialValuesApplied = true
    }

    fun morphInto(
        duration: Long = DEFAULT_DURATION,
        onStart: Action = null,
        onEnd: Action = null,
        offsetTrigger: OffsetTrigger? = null
    ) {

        if (isMorphing)
            return

        isMorphed = false
        isMorphing = true

        val doOnEnd = {
            onEnd?.invoke()

            applyProps(endView, endingState)

            mappings.forEach {
                applyProps(it.endView, it.endProps)
            }

            isMorphing = false
            isMorphed = true

            endingView.view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }

        performSetup()

        curveTranslator.setStartPoint(startingState.getDeltaCoordinates())
        curveTranslator.setEndPoint(endingState.getDeltaCoordinates())

        curveTranslator.setControlPoint(Coordintates(endingState.translationX, startingState.translationY))

        mappings.forEach {

            when {
                all(it.endView, it.startView) { all -> all.hasBitmapDrawable() } -> {

                    val transitionDrawable = MorphTransitionDrawable(it.startProps.background, it.endProps.background)

                    it.endView.morphBackground = transitionDrawable

                    transitionDrawable.startDrawableType = it.startView.getBackgroundType()
                    transitionDrawable.endDrawableType = it.endView.getBackgroundType()

                    transitionDrawable.isCrossFadeEnabled = true
                }
                all(it.startView, it.endView) { all -> all.hasVectorDrawable() } -> {

                    val fromBitmap = BitmapDrawable(context.resources, it.startProps.background?.toBitmap())
                    val toBitmap = BitmapDrawable(context.resources, it.endProps.background?.toBitmap())

                    val transitionDrawable = MorphTransitionDrawable(fromBitmap, toBitmap)

                    it.endView.morphBackground = transitionDrawable

                    transitionDrawable.startDrawableType = it.startView.getBackgroundType()
                    transitionDrawable.endDrawableType = it.endView.getBackgroundType()

                    transitionDrawable.isSequentialFadeEnabled = true
                }
                it.endView.hasMorphTransitionDrawable() -> {

                    val fromBitmap = BitmapDrawable(context.resources, it.startProps.background?.toBitmap())

                    val transitionDrawable = it.endView.getMorphTransitionDrawable()

                    if (transitionDrawable.startDrawableType == MorphTransitionDrawable.DrawableType.VECTOR ) {
                        transitionDrawable.setStartDrawable(fromBitmap)
                    }

                    transitionDrawable.setUpTransition(false)
                }
            }
        }

        endingView.morphVisibility = View.VISIBLE
        startingView.morphVisibility = View.INVISIBLE

        morph(
            endingView,
            startingState,
            endingState,
            mappings,
            morphIntoInterpolator,
            curveTranslator,
            duration,
            onStart,
            doOnEnd,
            offsetTrigger,
            AnimationType.REVEAL,
            MorphType.INTO
        )
    }

    fun morphFrom(
        duration: Long = DEFAULT_DURATION,
        onStart: Action = null,
        onEnd: Action = null,
        offsetTrigger: OffsetTrigger? = null
    ) {

        if (isMorphing)
            return

        if (!mappingsCreated) {
            createMappings()
            mappingsCreated = true
        }

        isMorphed = true
        isMorphing = true

        val doOnEnd = {
            onEnd?.invoke()

            startingView.morphVisibility = View.VISIBLE
            endingView.morphVisibility = View.INVISIBLE

            applyProps(endingView, endingState)

            mappings.forEach {
                applyProps(it.endView, it.endProps)
            }

            isMorphing = false
            isMorphed = false
        }

        curveTranslator.setStartPoint(endingState.getDeltaCoordinates())
        curveTranslator.setEndPoint(startingState.getDeltaCoordinates())

        curveTranslator.setControlPoint(Coordintates(endingState.translationX, startingState.translationY))

        mappings.forEach {
            when {
                all(it.endView, it.startView) { all -> all.hasBitmapDrawable() } -> {

                    val transitionDrawable = MorphTransitionDrawable(it.endProps.background, it.startProps.background)

                    it.endView.morphBackground = transitionDrawable

                    transitionDrawable.startDrawableType = it.startView.getBackgroundType()
                    transitionDrawable.endDrawableType = it.endView.getBackgroundType()

                    transitionDrawable.isCrossFadeEnabled = true
                }
                all(it.startView, it.endView) { all -> all.hasVectorDrawable() } -> {

                    val fromBitmap = BitmapDrawable(context.resources, it.startProps.background?.toBitmap())
                    val toBitmap = BitmapDrawable(context.resources, it.endProps.background?.toBitmap())

                    val transitionDrawable = MorphTransitionDrawable(toBitmap, fromBitmap)

                    it.endView.morphBackground = transitionDrawable

                    transitionDrawable.startDrawableType = it.startView.getBackgroundType()
                    transitionDrawable.endDrawableType = it.endView.getBackgroundType()

                    transitionDrawable.isSequentialFadeEnabled = true
                }
                it.endView.hasMorphTransitionDrawable() -> {
                    val fromBitmap = BitmapDrawable(context.resources, it.startProps.background?.toBitmap())

                    val transitionDrawable = it.endView.getMorphTransitionDrawable()

                    if (transitionDrawable.startDrawableType == MorphTransitionDrawable.DrawableType.VECTOR ) {
                        transitionDrawable.setStartDrawable(fromBitmap)
                    }

                    transitionDrawable.setUpTransition(true)
                }
            }
        }

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
            AnimationType.CONCEAL,
            MorphType.FROM
        )
    }

    private var lastOffset = MIN_OFFSET

    private fun transitionBy(fraction: Float) {

        if (!initialValuesApplied) {
            performSetup()
        }

        val startingProps = startingState
        val endingProps = endingState

        animateProperties(endView, startingProps, endingProps, fraction)

        endView.animator().translationX(startingProps.translationX + (endingProps.translationX - startingProps.translationX) * fraction).setDuration(0).start()
        endView.animator().translationY(startingProps.translationY + (endingProps.translationY - startingProps.translationY) * fraction).setDuration(0).start()

        if (morphChildren && mappings.isNotEmpty()) {
            for (mapping in mappings) {
                animateProperties(mapping.endView, mapping.startProps, mapping.endProps, fraction)

                mapping.endView.animator().x(mapping.startProps.x + (mapping.endProps.x - mapping.startProps.x) * fraction).setDuration(0).start()
                mapping.endView.animator().y(mapping.startProps.y + (mapping.endProps.y - mapping.startProps.y) * fraction).setDuration(0).start()
            }
        }

        if (children == null) {
            children = getAllChildren(endingView, false) { it.tag == null}
        }

        children?.let { list ->
            for (it in list) {
                it.animate()
                    .alpha(MIN_OFFSET + (MAX_OFFSET - MIN_OFFSET) * fraction )
                    .setDuration(0)
                    .start()
            }
        }

        lastOffset = fraction
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
        animationType: AnimationType,
        morphType: MorphType
    ) {

        this.remainingDuration = duration

        val animator: ValueAnimator = ValueAnimator.ofFloat(MIN_OFFSET, MAX_OFFSET)

        animator.addListener(MorphAnimationListener(onStart, onEnd))
        animator.addUpdateListener {

            val fraction = (it.animatedValue as Float).clamp(MIN_OFFSET, MAX_OFFSET)

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

            when (animationType) {
                AnimationType.REVEAL -> {
                    if (animateChildren && !childrenRevealed && fraction >= endStateMorphIntoDescriptor.animateOnOffset) {
                        childrenRevealed = true
                        animateChildren(endStateMorphIntoDescriptor, endingView, morphChildren, remainingDuration)
                    }
                }
                AnimationType.CONCEAL -> {
                    if (animateChildren && childrenRevealed && fraction >= endStateMorphFromDescriptor.animateOnOffset) {
                        childrenRevealed = false
                        animateChildren(endStateMorphFromDescriptor, endingView, morphChildren, remainingDuration)
                    }
                }
            }

            if (trigger != null && !trigger.hasTriggered) {
                if (fraction >= trigger.percentage) {
                    trigger.triggerAction?.invoke()
                    trigger.hasTriggered = true
                }
            }
            transitionOffsetListener?.invoke(fraction)
        }

        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }

    private fun tooDifferent(arg1: Float, arg2: Float, limit: Float): Boolean {
        return abs(arg2 - arg1) > limit
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

    private fun getChildMappings(
        startView: MorphLayout,
        endView: MorphLayout
    ): List<MorphMap> {

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

    private fun animateChildren(
        animDescriptor: AnimationDescriptor,
        parentView: MorphLayout,
        skipTagged: Boolean,
        inDuration: Long
    ) {
        if (!parentView.hasChildren())
            return

        val children = getAllChildren(parentView, !skipTagged) { it.tag == null }

        val duration = animDescriptor.duration ?: inDuration

        when (animDescriptor.type) {
            AnimationType.REVEAL -> {
                animateChildren(children, animDescriptor, duration)
            }
            AnimationType.CONCEAL -> {
                animateChildren(children, animDescriptor, duration)
            }
        }
    }

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

        if (view.hasGradientDrawable() && view.mutateCorners) {
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
        morphView.morphAlpha = startingProps.alpha + (endingProps.alpha - startingProps.alpha) * fraction

        morphView.morphScaleX = startingProps.scaleX + (endingProps.scaleX - startingProps.scaleX) * fraction
        morphView.morphScaleY = startingProps.scaleY + (endingProps.scaleY - startingProps.scaleY) * fraction

        morphView.morphPivotX = startingProps.pivotX + (endingProps.pivotX - startingProps.pivotX) * fraction
        morphView.morphPivotY = startingProps.pivotY + (endingProps.pivotY - startingProps.pivotY) * fraction

        morphView.morphRotation = startingProps.rotation + (endingProps.rotation - startingProps.rotation) * fraction

        morphView.morphRotationX = startingProps.rotationX + (endingProps.rotationX - startingProps.rotationX) * fraction
        morphView.morphRotationY = startingProps.rotationY + (endingProps.rotationY - startingProps.rotationY) * fraction

        morphView.morphTranslationZ = startingProps.translationZ + (endingProps.translationZ - startingProps.translationZ) * fraction

        morphView.morphElevation = startingProps.elevation + (endingProps.elevation - startingProps.elevation) * fraction

        if (morphView.mutateCorners && morphView.hasGradientDrawable() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            morphView.updateCorners(0, startingProps.cornerRadii[0] + (endingProps.cornerRadii[0] - startingProps.cornerRadii[0]) * fraction)
            morphView.updateCorners(1, startingProps.cornerRadii[1] + (endingProps.cornerRadii[1] - startingProps.cornerRadii[1]) * fraction)
            morphView.updateCorners(2, startingProps.cornerRadii[2] + (endingProps.cornerRadii[2] - startingProps.cornerRadii[2]) * fraction)
            morphView.updateCorners(3, startingProps.cornerRadii[3] + (endingProps.cornerRadii[3] - startingProps.cornerRadii[3]) * fraction)
            morphView.updateCorners(4, startingProps.cornerRadii[4] + (endingProps.cornerRadii[4] - startingProps.cornerRadii[4]) * fraction)
            morphView.updateCorners(5, startingProps.cornerRadii[5] + (endingProps.cornerRadii[5] - startingProps.cornerRadii[5]) * fraction)
            morphView.updateCorners(6, startingProps.cornerRadii[6] + (endingProps.cornerRadii[6] - startingProps.cornerRadii[6]) * fraction)
            morphView.updateCorners(7, startingProps.cornerRadii[7] + (endingProps.cornerRadii[7] - startingProps.cornerRadii[7]) * fraction)
        }

        if (morphView.hasMorphTransitionDrawable()) {
            morphView.getMorphTransitionDrawable().updateTransition(fraction)
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

    private fun getAllChildren(
        view: MorphLayout,
        deepSearch: Boolean,
        predicate: ((View) -> Boolean)? = null
    ): List<View> {

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

    private fun <T: View> animateChildren(
        inChildren: List<T>,
        descriptor: AnimationDescriptor,
        totalDuration: Long
    ) {
        if (!inChildren.any())
            return

        val offsetMultiplier = descriptor.durationMultiplier
        val startDelay = descriptor.delay ?: 0L

        val startStateProps = descriptor.startStateProps
        val endStateProps = descriptor.endStateProps

        val durationDelta = when (descriptor.type) {
            AnimationType.REVEAL -> (totalDuration + (totalDuration * offsetMultiplier)).roundToLong()
            AnimationType.CONCEAL -> (totalDuration * offsetMultiplier).roundToLong()
        }

        val children = if (descriptor.reversed) inChildren.reversed() else inChildren

        if (descriptor.stagger != null) {
            descriptor.stagger?.let { stagger ->
                val delayAdd = (durationDelta * stagger.multiplier).toLong()
                val duration = durationDelta - (delayAdd / children.count())
                var delay = startDelay
                children.forEach {
                    it.visibility = startStateProps.visibility
                    it.translationY = startStateProps.translationY
                    it.scaleX = startStateProps.scaleX
                    it.scaleY = startStateProps.scaleY
                    it.alpha = startStateProps.alpha
                    it.animate()
                        .setListener(null)
                        .setDuration((duration + (duration * offsetMultiplier)).roundToLong())
                        .setStartDelay(delay)
                        .alpha(endStateProps.alpha)
                        .scaleX(endStateProps.scaleX)
                        .scaleY(endStateProps.scaleY)
                        .translationY(endStateProps.translationY)
                        .setInterpolator(descriptor.interpolator)
                        .start()
                    delay += delayAdd
                }
            }
        } else {
            children.forEach {
                it.visibility = startStateProps.visibility
                it.translationY = startStateProps.translationY
                it.scaleX = startStateProps.scaleX
                it.scaleY = startStateProps.scaleY
                it.alpha = startStateProps.alpha
                it.animate()
                    .setListener(null)
                    .setDuration(durationDelta)
                    .setStartDelay(startDelay)
                    .alpha(endStateProps.alpha)
                    .scaleX(endStateProps.scaleX)
                    .scaleY(endStateProps.scaleY)
                    .translationY(endStateProps.translationY)
                    .setInterpolator(descriptor.interpolator)
                    .start()
            }
        }
    }

    companion object {

        const val MAX_OFFSET: Float = 1f
        const val MIN_OFFSET: Float = 0f

        const val DEFAULT_DURATION: Long = 350L

        const val DEFAULT_CHILDREN_REVEAL_OFFSET: Float = 0.60f
        const val DEFAULT_CHILDREN_CONCEAL_OFFSET: Float = 0.0f

        const val DEFAULT_REVEAL_DURATION_MULTIPLIER: Float = 0.2f
        const val DEFAULT_CONCEAL_DURATION_MULTIPLIER: Float = 0.4f
    }

    private data class MorphMap(
        var startView: MorphLayout,
        var endView: MorphLayout,
        var startProps: Properties,
        var endProps: Properties
    )

    data class OffsetTrigger(
        val percentage: Float,
        val triggerAction: Action
    ) {
        var hasTriggered: Boolean = false
    }

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
        var color: Int,
        val stateList: ColorStateList?,
        val cornerRadii: CornerRadii,
        val windowLocationX: Int,
        val windowLocationY: Int,
        val background: Drawable?,
        val hasVectorBackground: Boolean,
        val hasBitmapBackground: Boolean,
        val hasGradientBackground: Boolean,
        val tag: String
    ) {
        fun getDeltaCoordinates() = Coordintates(translationX, translationY)

        override fun toString() = tag
    }

    data class AnimationStagger(
        val multiplier: Float = 0.2f
    )

    data class AnimationDescriptor(
        var type: AnimationType,
        var animateOnOffset: Float,
        var durationMultiplier: Float,
        var startStateProps: AnimationProperties,
        var endStateProps: AnimationProperties,
        var interpolator: TimeInterpolator,
        var stagger: AnimationStagger? = null,
        var reversed: Boolean = false,
        var duration: Long? = null,
        var delay: Long? = null
    )

    data class AnimationProperties(
        var alpha: Float = 1f,
        var elevation: Float = 0f,
        var translationX: Float = 0f,
        var translationY: Float = 0f,
        var translationZ: Float = 0f,
        var rotation: Float = 0f,
        var rotationX: Float = 0f,
        var rotationY: Float = 0f,
        var scaleX: Float = 1f,
        var scaleY: Float = 1f,
        var visibility: Int = View.VISIBLE
    ) {
        fun reset() {
            alpha = 1f
            elevation = 0f
            translationX = 0f
            translationY = 0f
            translationZ = 0f
            rotation = 0f
            rotationX = 0f
            rotationY = 0f
            scaleX = 1f
            scaleY = 1f
            visibility = View.VISIBLE
        }

        companion object {
            fun defaultInPropsStart(): AnimationProperties {
                return AnimationProperties(
                    alpha = 0f,
                    scaleX = 0.8f,
                    scaleY = 0.8f,
                    translationY = -(8.dp)
                    )
            }
            fun defaultInPropsEnd(): AnimationProperties {
                return AnimationProperties(
                    alpha = 1f,
                    scaleX = 1f,
                    scaleY = 1f,
                    translationY = 0f
                )
            }
            fun defaultOutPropsStart(): AnimationProperties {
                return AnimationProperties(
                    alpha = 1f,
                    scaleX = 1f,
                    scaleY = 1f,
                    translationY = 0f
                )
            }
            fun defaultOutPropsEnd(): AnimationProperties {
                return AnimationProperties(
                    alpha = 0f,
                    scaleX = 0.8f,
                    scaleY = 0.8f,
                    translationY = (5.dp)
                )
            }
        }
    }

    enum class MorphType { INTO, FROM }

    enum class AnimationType { REVEAL, CONCEAL }

}
