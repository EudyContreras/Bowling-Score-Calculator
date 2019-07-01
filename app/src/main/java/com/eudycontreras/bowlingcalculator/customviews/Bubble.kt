package com.eudycontreras.bowlingcalculator.customviews

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.bowlingcalculator.utilities.extensions.dp
import com.eudycontreras.bowlingcalculator.utilities.mapRange
import com.eudycontreras.bowlingcalculator.utilities.properties.*

class Bubble: Shape(), TouchableShape {

    enum class Pointer {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }
    override fun onTouch(event: MotionEvent, x: Float, y: Float) {
        touchProcessor?.invoke(this, event, x, y)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {}

    var pointer = Pointer.BOTTOM

    var pointerOffset = 0.5f

    var pointerWidth = 10.dp

    var pointerLength = 10.dp

    var cornerRadius = 8.dp
        set(value) {
            field = value
            corners.rx = field / 2
            corners.ry = field / 2
        }

    private val pathPlot: PathPlot = PathPlot(Path())

    fun build(bounds: Bounds) {
        val offset = (this.elevation * 2)

        val width = bounds.width - offset
        val height = bounds.height - (this.elevation + this.pointerLength)

        this.bounds.dimension = Dimension(width, height)
        this.bounds.coordinate = Coordinate(bounds.width / 2, (bounds.top - this.cornerRadius))
    }

    override fun render(path: Path, paint: Paint, canvas: Canvas) {
        if (!render) {
            return
        }

        val offsetLeft = 1f * mapRange(pointerOffset, 0f, 0.5f, 0f, 0.5f, 0f, 1f)
        val offsetRight = 1f * mapRange(pointerOffset, 0.5f, 1f, 0.5f, 0f, 0f, 1f)

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
        pathPlot.startY= top + cornerRadius

        val shift = (pathPlot.width - pointerWidth)

        pathPlot.points[0].startX = -(pointerWidth * offsetLeft)
        pathPlot.points[1].startX =  -((pointerOffset * shift))
        pathPlot.points[9].startX = -(shift-(pointerOffset * shift))
        pathPlot.points[10].startX = -(pointerWidth * offsetRight)

        pathPlot.contentBounds.coordinate.x = left - ((pathPlot.width/2) + (cornerRadius/2))
        pathPlot.contentBounds.coordinate.y = top - (pointerLength + (pathPlot.height + (cornerRadius / 2)))

        pathPlot.build()

        if (drawShadow) {
            //paint.setShadowLayer(elevation, 0f, 0f, shadow!!.shadowColor.updateAlpha(255).toColor())
            shadow?.draw(this, paint, canvas)
        }

        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        canvas.drawPath(pathPlot.path, paint)

        if (showStroke) {

            strokeColor?.let {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                canvas.drawPath(pathPlot.path, paint)
            }
        }
    }
}