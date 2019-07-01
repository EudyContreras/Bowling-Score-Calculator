package com.eudycontreras.bowlingcalculator.utilities.properties

import android.graphics.*
import com.eudycontreras.bowlingcalculator.customviews.Bubble
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import com.eudycontreras.bowlingcalculator.utilities.mapRange


/**
 * Created by eudycontreras.
 */

class Shadow {

    private var shadowColorStart = MutableColor.fromColor(DefaultColor)
    private var shadowColorEnd = MutableColor.fromColor(DefaultColor)

    companion object {
        const val DEFAULT_MAX_STEP_COUNT = 20f
        const val DEFAULT_MIN_STEP_COUNT = 1f

        val DefaultColor: Color = MutableColor.rgb(20, 20, 20)
    }

    var shadowColor: MutableColor
        get() = shadowColorStart
        set(value) {
            shadowColorStart.setColor(value).updateAlpha(35)
            shadowColorEnd.setColor(value).updateAlpha(15)
        }

    var minStepCount = DEFAULT_MIN_STEP_COUNT
        set(value) {
            field = when {
                value < DEFAULT_MIN_STEP_COUNT -> DEFAULT_MIN_STEP_COUNT
                value > maxStepCount -> maxStepCount
                else -> value
            }
        }

    var maxStepCount = DEFAULT_MAX_STEP_COUNT - 2
        set(value) {
            field = when {
                value < DEFAULT_MIN_STEP_COUNT -> DEFAULT_MIN_STEP_COUNT
                value > DEFAULT_MAX_STEP_COUNT -> DEFAULT_MAX_STEP_COUNT
                else -> value
            }
        }

    var scaleMatrix = Matrix()

    var rectF = RectF()

    var computed: Boolean = false

    private var color: MutableColor = MutableColor.fromColor(shadowColorStart)

    private var steps = 0

    init {
        shadowColor = MutableColor.fromColor(DefaultColor)
    }

    private val pathPlot: PathPlot = PathPlot(Path())

    fun draw(bubble: Bubble, paint: Paint, canvas: Canvas) {

        var steps = mapRange(
            bubble.elevation,
            Shape.MIN_ELEVATION.dp,
            Shape.MAX_ELEVATION.dp,
            minStepCount,
            maxStepCount
        ).toInt()

        if (steps <= 0) steps = 1

        for (i: Int in 0..bubble.elevation.toInt() step steps) {

            val amount = mapRange(
                i.toFloat(),
                0f,
                bubble.elevation,
                shadowColorStart.getOpacity(),
                shadowColorEnd.getOpacity()
            )

            color.updateAlpha(amount)
            paint.color = color.toColor()

            val path = getPathShape(pathPlot, bubble, i)

            canvas.drawPath(path, paint)

            path.reset()
        }
    }

    private fun getPathShape(pathPlot: PathPlot, bubble: Bubble, offset: Int): Path {
        val pointerOffset = bubble.pointerOffset

        val offsetLeft = 1f * mapRange(pointerOffset, 0f, 0.5f, 0f, 0.5f, 0f, 1f)
        val offsetRight = 1f * mapRange(pointerOffset, 0.5f, 1f, 0.5f, 0f, 0f, 1f)

        val pointerWidth = bubble.pointerWidth
        val pointerLength = bubble.pointerLength
        val cornerRadius = bubble.cornerRadius
        val dimension = bubble.dimension.copyProps()

        dimension.width = dimension.width
        dimension.height = dimension.height

        val left = bubble.left
        val top = bubble.top

        if (!pathPlot.pathCreated) {

            pathPlot.width = dimension.width - (cornerRadius * 2)
            pathPlot.height = dimension.height - (cornerRadius * 2)

            pathPlot.contentBounds.dimension.width = pathPlot.width + cornerRadius
            pathPlot.contentBounds.dimension.height = pathPlot.height + cornerRadius

            val shift = (pathPlot.width - pointerWidth)

            pathPlot.startX = left
            pathPlot.startY = top + cornerRadius

            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(pointerWidth * offsetLeft), -pointerLength))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -((pointerOffset * shift)), 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, -cornerRadius, 0f, -cornerRadius, -cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, 0f, -pathPlot.height))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, 0f, -cornerRadius, cornerRadius, -cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, pathPlot.width, 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, cornerRadius, 0f, cornerRadius, cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, 0f, pathPlot.height))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, 0f, cornerRadius, -cornerRadius, cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(shift - (pointerOffset * shift)), 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(pointerWidth * offsetRight), pointerLength))

            pathPlot.build()
        }

        pathPlot.startX = left
        pathPlot.startY = top + cornerRadius

        val shift = (pathPlot.width - pointerWidth)

        pathPlot.points[0].startX = -(pointerWidth * offsetLeft)
        pathPlot.points[1].startX =  -((pointerOffset * shift))
        pathPlot.points[9].startX = -(shift-(pointerOffset * shift))
        pathPlot.points[10].startX = -(pointerWidth * offsetRight)

        pathPlot.contentBounds.coordinate.x = left - ((pathPlot.width/2) + (cornerRadius/2))
        pathPlot.contentBounds.coordinate.y = top - (pointerLength + (pathPlot.height + (cornerRadius / 2)))

        pathPlot.build()

        val path = pathPlot.path

        if (!computed) {
            path.computeBounds(rectF, true)
            computed = true
        }

        val ratio = rectF.width() / rectF.height()

        scaleMatrix.setScale(1f  + ((offset.toFloat() / 140f) / ratio), 1.0f + (offset.toFloat() / 100f), rectF.centerX(), rectF.centerY() - ((bubble.elevation * 4) + offset))
        path.transform(scaleMatrix)

        return path
    }
}