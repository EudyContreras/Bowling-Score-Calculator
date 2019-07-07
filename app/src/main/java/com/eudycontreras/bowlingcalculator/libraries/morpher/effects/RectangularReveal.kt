package com.eudycontreras.bowlingcalculator.libraries.morpher.effects

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews.morphLayouts.ConstraintLayout
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.DrawDispatchListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.listeners.MorphAnimationListener
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Bounds
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.CornerRadii
import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Dimension

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
class RectangularReveal private constructor(private val target: View) : DrawDispatchListener {

    private val backgroundColor = Color.TRANSPARENT

    var usableWidth: Int = 0
        private set

    var usableHeight: Int = 0
        private set

    private val revealDelay: Long = 0L

    val revealDuration: Long = 7500L

    var revealX: Float = Float.MIN_VALUE
    var revealY: Float = Float.MIN_VALUE

    var revealTargetX = 0f
    var revealTargetY = 0f

    var revealMinWidth: Float = 0f
    var revealMinHeight: Float = 0f

    var revealMaxWidth = -1f
    var revealMaxHeight = -1f

    var topLeftMinRadius: Float = 0f
    var topRightMinRadius: Float = 0f
    var bottomLeftMinRadius: Float = 0f
    var bottomRightMinRadius: Float = 0f

    var topLeftMaxRadius: Float = 0f
    var topRightMaxRadius: Float = 0f
    var bottomLeftMaxRadius: Float = 0f
    var bottomRightMaxRadius: Float = 0f

    private var minRadii: FloatArray = FloatArray(4)
    private var maxRadii: FloatArray = FloatArray(4)

    private var animationRunning: Boolean = false
    private var autoStartReveal = false

    private var isCleanUpAfter: Boolean = false

    private var onStart: (() -> Unit)? = null
    private var onEnd: (() -> Unit)? = null

    private var listener: ViewDrawListener? = null

    private var bounds: Bounds? = null

    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    private var revealParticle: ParticleReveal? = null

    private val animationListener =  MorphAnimationListener {

    }

    interface ViewDrawListener {
        fun onViewDraw(view: View)
    }

    init {

        if (target is ConstraintLayout) {
            target.setListener(this)
        }
    }

    private fun initializeReveal() {
        revealParticle = ParticleReveal()

        revealParticle?.let {

            minRadii[0] = topLeftMinRadius
            minRadii[1] = topRightMinRadius
            minRadii[2] = bottomRightMinRadius
            minRadii[3] = bottomLeftMinRadius

            maxRadii[0] = topLeftMaxRadius
            maxRadii[1] = topRightMaxRadius
            maxRadii[2] = bottomRightMaxRadius
            maxRadii[3] = bottomLeftMaxRadius

            it.paint = Paint()
            it.targetX = revealTargetX
            it.targetY = revealTargetY
            it.setMinWidth(revealMinWidth)
            it.setMinHeight(revealMinHeight)
            it.setMaxWidth(revealMaxWidth)
            it.setMaxHeight(revealMaxHeight)
            it.setMinRadii(minRadii)
            it.setMaxRadii(maxRadii)
            it.init()
        }

        animator.addUpdateListener { animation ->
            revealParticle?.let {
                minRadii[0] = topLeftMinRadius
                minRadii[1] = topRightMinRadius
                minRadii[2] = bottomRightMinRadius
                minRadii[3] = bottomLeftMinRadius

                maxRadii[0] = topLeftMaxRadius
                maxRadii[1] = topRightMaxRadius
                maxRadii[2] = bottomRightMaxRadius
                maxRadii[3] = bottomLeftMaxRadius

                it.setX(revealX)
                it.setY(revealY)
                it.targetX = revealTargetX
                it.targetY = revealTargetY
                it.setMinWidth(revealMinWidth)
                it.setMinHeight(revealMinHeight)
                it.setMaxWidth(revealMaxWidth)
                it.setMaxHeight(revealMaxHeight)
                it.setMinRadii(minRadii)
                it.setMaxRadii(maxRadii)
                it.update(revealDuration, animation.animatedValue as Float)
            }

            target.invalidate()
        }
    }

    private fun startRevealAnimation() {
        stopRevealAnimation()

        if (!animationRunning) {
            animationRunning = true
            animator.addListener(animationListener)
            animator.duration = revealDuration
            animator.startDelay = revealDelay
            animator.start()
        }
    }

    private fun stopRevealAnimation() {
        if (animationRunning) {
            animationRunning = false
            animator.end()
        }
    }

    private fun initializeValues(): Bounds {
        val width = target.width
        val height = target.height

        val paddingLeft = target.paddingLeft
        val paddingRight = target.paddingRight
        val paddingTop = target.paddingTop
        val paddingBottom = target.paddingBottom

        usableWidth = width - (paddingLeft + paddingRight)
        usableHeight = height - (paddingTop + paddingBottom)

        if (revealMaxWidth == -1f) {
            revealMaxWidth = usableWidth.toFloat()
        }
        if (revealMaxHeight == -1f) {
            revealMaxHeight = usableHeight.toFloat()
        }

        revealParticle!!.setMaxWidth(revealMaxWidth)
        revealParticle!!.setMaxHeight(revealMaxHeight)

        return Bounds(0f, 0f, usableWidth.toFloat(), usableHeight.toFloat())
    }

    private fun drawReveal(canvas: Canvas) {
        canvas.drawColor(backgroundColor)

        if (bounds == null) {

            bounds = initializeValues()

            revealParticle?.let {
                it.setX(revealX)
                it.setY(revealY)
                it.setMinWidth(revealMinWidth)
                it.setMinHeight(revealMinHeight)
                it.setMaxWidth(revealMaxWidth)
                it.setMaxHeight(revealMaxHeight)
                it.setMinRadii(minRadii)
                it.setMaxRadii(maxRadii)
                it.bounds = bounds!!
                it.update()
                it.init()
            }

            if (listener != null) {
                listener!!.onViewDraw(target)
            }
            if (autoStartReveal) {
                startRevealAnimation()
            }
        }

        revealParticle!!.draw(canvas)
    }

    override fun onDrawDispatched(canvas: Canvas) {
        drawReveal(canvas)
    }

    override fun onDraw(canvas: Canvas) {}

    fun setListener(listener: ViewDrawListener) {
        this.listener = listener
    }

    companion object {
        fun createRectangularReveal(viewGroup: View, fromX: Float = -1f, fromY: Float = -1f): Animator {
            val startDimension = Dimension()
            val endDimension = Dimension()

            endDimension.width = viewGroup.width.toFloat()
            endDimension.height = viewGroup.height.toFloat()

            return createRectangularReveal(
                viewGroup,
                fromX,
                fromY,
                startDimension,
                endDimension
            )
        }

        fun createRectangularReveal(viewGroup: View, startDimension: Dimension, endDimension: Dimension): Animator {
            return createRectangularReveal(
                viewGroup,
                -1f,
                -1f,
                startDimension,
                endDimension
            )
        }

        fun createRectangularReveal(
            viewGroup: View,
            fromX: Float,
            fromY: Float,
            startDimension: Dimension,
            endDimension: Dimension
        ): Animator {
            return createRectangularReveal(
                viewGroup,
                fromX,
                fromY,
                0f,
                0f,
                startDimension,
                endDimension
            )
        }

        fun createRectangularReveal(
            viewGroup: View,
            fromX: Float,
            fromY: Float,
            toX: Float,
            toY: Float,
            startDimension: Dimension,
            endDimension: Dimension
        ): Animator {
            val radii = FloatArray(4)
            return createRectangularReveal(
                viewGroup,
                fromX,
                fromY,
                toX,
                toY,
                startDimension,
                endDimension,
                radii,
                radii
            )
        }

        fun createRectangularReveal(
            viewGroup: View,
            fromX: Float,
            fromY: Float,
            toX: Float,
            toY: Float,
            startDimension: Dimension,
            endDimension: Dimension,
            radii: FloatArray
        ): Animator {
            return createRectangularReveal(
                viewGroup,
                fromX,
                fromY,
                toX,
                toY,
                startDimension,
                endDimension,
                radii,
                radii
            )
        }

        fun createRectangularReveal(
            viewGroup: View,
            fromX: Float,
            fromY: Float,
            toX: Float,
            toY: Float,
            startDimension: Dimension,
            endDimension: Dimension,
            startRadii: FloatArray,
            endRadii: FloatArray
        ): Animator {
            return createRectangularReveal(
                viewGroup,
                fromX,
                fromY,
                toX,
                toY,
                startDimension,
                endDimension,
                CornerRadii(startRadii),
                CornerRadii(endRadii)
            )
        }

        fun createRectangularReveal(
            viewGroup: View,
            fromX: Float,
            fromY: Float,
            toX: Float,
            toY: Float,
            startDimension: Dimension,
            endDimension: Dimension,
            startRadii: CornerRadii?,
            endRadii: CornerRadii?
        ): Animator {
            val rectangularReveal =
                RectangularReveal(viewGroup)

            rectangularReveal.revealX = fromX
            rectangularReveal.revealY = fromY
            rectangularReveal.revealTargetX = toX
            rectangularReveal.revealTargetY = toY
            rectangularReveal.revealMinWidth = startDimension.width
            rectangularReveal.revealMinHeight = startDimension.height
            rectangularReveal.revealMaxWidth = endDimension.width
            rectangularReveal.revealMaxHeight = endDimension.height
            rectangularReveal.topLeftMinRadius = startRadii?.topLeft ?: 0f
            rectangularReveal.topRightMinRadius = startRadii?.topRight ?: 0f
            rectangularReveal.bottomRightMinRadius = startRadii?.bottomRight ?: 0f
            rectangularReveal.bottomLeftMinRadius = startRadii?.bottomLeft ?: 0f
            rectangularReveal.topLeftMaxRadius = endRadii?.topLeft ?: 0f
            rectangularReveal.topRightMaxRadius = endRadii?.topRight ?: 0f
            rectangularReveal.bottomRightMaxRadius = endRadii?.bottomRight ?: 0f
            rectangularReveal.bottomLeftMaxRadius = endRadii?.bottomLeft ?: 0f
            rectangularReveal.initializeReveal()

            return rectangularReveal.animator
        }
    }
}