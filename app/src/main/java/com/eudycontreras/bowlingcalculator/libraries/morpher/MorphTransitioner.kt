package com.eudycontreras.bowlingcalculator.libraries.morpher

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.MorphLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.extensions.getProperties
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.MorphAnimationListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Coordintates
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.libraries.morpher.utilities.Action
import com.eudycontreras.bowlingcalculator.libraries.morpher.utilities.ColorUtility
import com.eudycontreras.bowlingcalculator.libraries.morpher.utilities.CurvedTranslator
import com.eudycontreras.bowlingcalculator.libraries.morpher.utilities.OffsetListener
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import com.eudycontreras.bowlingcalculator.utilities.extensions.toStateList
import kotlin.math.abs
import kotlin.math.roundToLong


class MorphTransitioner() {

    constructor(fromView: MorphLayout, toView: MorphLayout): this() {
        startingView = fromView
        endingView = toView
    }

    lateinit var startingView: MorphLayout

    lateinit var endingView: MorphLayout

    private var remainingDuration: Long = 0L

    private var percentage = MIN_PERCENTAGE

    private var morphMaps: ArrayList<MorphMap> = ArrayList()

    private var startingChildViews: ArrayList<View> = ArrayList()
    private var endingChildViews: ArrayList<View> = ArrayList()

    private var offsetListener: OffsetListener = null
    private var onTransitionEnd: Action = null

    private var initialValuesApplied: Boolean = false

    var revealChildrenOffset: Float = DEFAULT_CHILDREN_REVEAL_OFFSET
        set(value) {
            if (value in 0f..1f) {
                field = value
            }
        }
    var durationOffsetMultiplier: Float = DEFAULT_REVEAL_DURATION_MULTIPLIER

    var animateRevealChildren: Boolean = true

    var childrenRevealed: Boolean = false
        private set

    init {
        if (this@MorphTransitioner::startingView.isInitialized && this@MorphTransitioner::endingView.isInitialized) {
            performSetup(true)
        }
    }

    private fun performSetup(hideUntagged: Boolean) {
        startingChildViews = getAllChildren(startingView as View)
        endingChildViews =  getAllChildren(endingView as View)

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

    private fun revealChildren(duration: Long) {
        childrenRevealed = true
        val children = endingChildViews.filter { it.visibility == View.INVISIBLE}
        animateRevealChildren(children.asSequence(), duration, durationOffsetMultiplier)
    }

    private fun applyInitialValues() {
        endingView.morphAlpha = 1f
        initialValuesApplied = true
    }

    private fun setOffset(offset: Float) {
        percentage = offset

        morphMaps.forEach {

            it.endView.morphX = (it.startProps.x + (it.endProps.x - it.startProps.x) * offset)
            it.endView.morphY = (it.startProps.y + (it.endProps.y - it.startProps.y) * offset)

            animateOffset(it.endView, it.startProps, it.endProps, offset)
        }

        offsetListener?.invoke(offset)
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

        performSetup(true)

        val animator: ValueAnimator = ValueAnimator.ofFloat(percentage, percent)

        val listener = MorphAnimationListener({ applyInitialValues() },onTransitionEnd)

        animator.addListener(listener)
        animator.addUpdateListener {
            val value = it.animatedValue as Float

            remainingDuration = duration - (duration * value).toLong()

            setOffset(value)

            if (animateRevealChildren && !childrenRevealed && value >= revealChildrenOffset) {
                revealChildren(duration)
            }

            if (trigger != null && !trigger.hasTriggered) {
                if (value >= trigger.percentage) {
                    trigger.triggerAction?.invoke()
                    trigger.hasTriggered = true
                }
            }
        }
        animator.duration = duration
        animator.startDelay = startDelay
        animator.interpolator = interpolator ?: DEFAULT_INTERPOLATOR
        animator.start()
    }

    fun doWhenTransitionEnds(action: Action) {
        this.onTransitionEnd = action
    }

    fun onOffsetChanged(progressListener: (percent: Float) -> Unit) {
        this.offsetListener = progressListener
    }

    fun setOffset(percent: Int) = setOffset(percent.toFloat() / 100f)

    class Morpher {
        private var curveTranslation = true
        private var morphChildren = true
        private var isMorphing = false
        private var isMorphed = false
        private var isSetup = false

        private lateinit var mappings: List<MorphMap>

        private lateinit var fromState: Properties
        private lateinit var toState: Properties

        private var offsetListener: OffsetListener = null

        private var remainingDuration: Long = 0L

        lateinit var startingView: MorphLayout
        lateinit var endingView: MorphLayout
        lateinit var targetView: MorphLayout

        var interpolatorMorphTo: Interpolator? = null
        var interpolatorMorphFrom: Interpolator? = null

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

        var durationOffsetMultiplier: Float = DEFAULT_REVEAL_DURATION_MULTIPLIER

        var animateChildren: Boolean = true

        var childrenRevealed: Boolean = false
            private set

        private fun setUp() {
            if (isSetup)
                return

            fromState = startingView.getProperties()
            toState = endingView.getProperties()

            mappings = if (morphChildren) {
                getChildMappings(startingView, endingView)
            } else emptyList()

            isSetup = true
        }

        fun morphInto(duration: Long, onStart: Action = null, onEnd: Action = null, offsetTrigger: OffsetTrigger? = null) {

            setUp()

            isMorphing = true

            for (child in endingView.getChildren()) {
                if (child.visibility == View.GONE)
                    continue

                if (morphChildren && child.tag != null)
                    continue

                child.alpha = 0f
            }

            applyProps(endingView, fromState)

            endingView.morphVisibility = View.VISIBLE
            startingView.morphVisibility = View.INVISIBLE

            val startX: Float = fromState.windowLocationX - (toState.width / 2f - fromState.width / 2f)
            val startY: Float = fromState.windowLocationY - (toState.height / 2f - fromState.height / 2f)

            val endX: Float = toState.windowLocationX.toFloat()
            val endY: Float = toState.windowLocationY.toFloat()

            val translationX: Float = abs(endX - startX)
            val translationY: Float = abs(endY - startY)

            fromState.translationX =  if (startX < endX) -translationX else translationX
            fromState.translationY =  if (startY < endY) -translationY else translationY

            endingView.morphTranslationX = fromState.translationX
            endingView.morphTranslationY = fromState.translationY

            val curveTranslator = CurvedTranslator()
            curveTranslator.setStartPoint(fromState.getDeltaCoordinates())
            curveTranslator.setEndPoint(toState.getDeltaCoordinates())

            val midPoint = Coordintates.midPoint(fromState.getDeltaCoordinates(), toState.getDeltaCoordinates())
            val crossPoint = Coordintates(fromState.translationX, toState.translationY)

            curveTranslator.setControlPoint(Coordintates.midPoint(midPoint, crossPoint))

            morph(
                endingView,
                fromState,
                toState,
                mappings,
                interpolatorMorphTo,
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
                startingView.morphVisibility = View.VISIBLE
                endingView.morphVisibility = View.INVISIBLE
            }

            val curveTranslator = CurvedTranslator()
            curveTranslator.setStartPoint(toState.getDeltaCoordinates())
            curveTranslator.setEndPoint(fromState.getDeltaCoordinates())

            val midPoint = Coordintates.midPoint(fromState.getDeltaCoordinates(), toState.getDeltaCoordinates())
            val crossPoint = Coordintates(fromState.translationX, toState.translationY)

            curveTranslator.setControlPoint(Coordintates.midPoint(midPoint, crossPoint))

            morph(
                endingView,
                toState,
                fromState,
                mappings,
                interpolatorMorphFrom,
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
            curveTranslator: CurvedTranslator,
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
            curveTranslator: CurvedTranslator
        ) {
            if (curveTranslation) {
                endView.morphTranslationX = curveTranslator.getCurvedTranslationX(offset).toFloat()
                endView.morphTranslationY = curveTranslator.getCurvedTranslationY(offset).toFloat()
            } else {
                endView.morphTranslationX = (startingProps.translationX + (endingProps.translationX - startingProps.translationX) * offset)
                endView.morphTranslationY = (startingProps.translationY + (endingProps.translationY - startingProps.translationY) * offset)
            }
        }

        private fun getChildMappings(startView: MorphLayout, endView: MorphLayout): List<MorphMap> {
            val startChildren = startView.getChildren().filter { it.tag != null }
            val endChildren = endView.getChildren().filter { it.tag != null }

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
            val children = if (skipTagged) endingView.getChildren().filter { it.tag == null } else endingView.getChildren()
            animateRevealChildren(children, duration, durationOffsetMultiplier)
        }

        private fun concealChildren(skipTagged: Boolean, duration: Long) {
            childrenRevealed = false
            val children = if (skipTagged) endingView.getChildren().filter { it.tag == null } else endingView.getChildren()
            animateConcealChildren(children, duration, durationOffsetMultiplier)
        }

        enum class MorphType {
            INTO, FROM
        }
    }

    enum class ChildrenAction { REVEAL, CONCEAL }

    companion object {

        const val MAX_PERCENTAGE: Float = 1f
        const val MIN_PERCENTAGE: Float = 0f

        const val DEFAULT_DURATION: Long = 300L

        const val DEFAULT_CHILDREN_REVEAL_OFFSET = 0.60f
        const val DEFAULT_CHILDREN_CONCEAL_OFFSET = 0.0f

        const val DEFAULT_REVEAL_DURATION_MULTIPLIER = 0.2f

        val DEFAULT_INTERPOLATOR: TimeInterpolator = FastOutSlowInInterpolator()

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

            if (morphView.showMutateCorners && !morphView.hasVectorDrawable() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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

        private fun getAllChildren(view: View, predicate: ((View) -> Boolean)? = null): ArrayList<View> {

            val visited = ArrayList<View>()
            val unvisited = arrayListOf(view)

            while (unvisited.isNotEmpty()) {
                val child = unvisited.removeAt(0)

                if(predicate != null) {
                    if (predicate(child)) {
                        visited.add(child)
                    }
                } else {
                    visited.add(child)
                }

                if (child !is ViewGroup)
                    continue

                (0 until child.childCount).mapTo(unvisited) { child.getChildAt(it) }
            }
            return visited
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
                    .translationY(12.dp)
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
