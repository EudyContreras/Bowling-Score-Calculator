package com.eudycontreras.bowlingcalculator.utilities.properties

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.view.MotionEvent

/**
 * Created by eudycontreras.
 */

abstract class Shape {

    val corners: CornerRadii by lazy { CornerRadii() }

    var render: Boolean = true

    var showStroke: Boolean = false
        get() = field && strokeWidth > 0

    var shadowPosition: LightSource.Position = LightSource.Position.BOTTOM_LEFT

    var touchProcessor: ((Shape, MotionEvent, Float, Float) -> Unit)? = null

    var color: MutableColor = MutableColor()

    open var bounds: Bounds = Bounds()

    var left: Float
        get() = coordinate.x
        set(value) {
            coordinate.x = value
        }

    var top: Float
        get() = coordinate.y
        set(value) {
            coordinate.y = value
        }

    var bottom: Float
        get() = top + dimension.height
        set(value) {
            dimension.height = value - top
        }

    var right: Float
        get() = left + dimension.width
        set(value) {
            dimension.width = value - left
        }

    var coordinate: Coordinate
        get() = bounds.coordinate
        set(value) {
            bounds.coordinate = value
        }

    var dimension: Dimension
        get() = bounds.dimension
        set(value) {
            bounds.dimension = value
        }

    var elevation: Float = 0f
        set(value) {
            field = value
            drawShadow = value > 0f
        }

    var drawShadow: Boolean = false
        get() = field && elevation > 0
        set(value) {
            field = value
            if (shadow == null) {
                if (value) {
                    shadow = Shadow()
                }
            }
        }

    var strokeWidth: Float = 0f

    var strokeColor: MutableColor? = null

    var shadow: Shadow? = null

    var shader: Shader? = null

    fun reset() {
        coordinate.x = 0f
        coordinate.y = 0f

        dimension.width = 0f
        dimension.height = 0f

        color.setColor(0, 0, 0, 255)

        elevation = 0f
        corners.reset()

        showStroke = false
        drawShadow = false
        shadowPosition = LightSource.Position.BOTTOM_LEFT

        strokeWidth = 0f
        strokeColor = null

        shadow = null
        shader = null
        render = true

    }

    fun update(delta: Float) {}

    abstract fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas
    )

    companion object {
        const val MAX_ELEVATION = 50
        const val MIN_ELEVATION = 0

        private fun getPositions(count: Int): FloatArray {
            val value: Float = 1f / (count - 1)
            val array = ArrayList<Float>()
            var increase = value
            array.add(0f)
            for (i in 0 until count - 1) {
                array.add(increase)
                increase += (value)
            }
            return array.toFloatArray()
        }
    }
}