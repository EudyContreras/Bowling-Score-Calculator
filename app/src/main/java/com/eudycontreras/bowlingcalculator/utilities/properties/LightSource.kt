package com.eudycontreras.bowlingcalculator.utilities.properties


/**
 * Created by eudycontreras.
 */


data class LightSource(
    var x: Float = 0f,
    var y: Float = 0f,
    var radius: Float = 0f,
    var intensity: Float = 0f,
    var color: MutableColor = MutableColor()
) {

    enum class Position {
        TOP_LEFT,
        TOP_RIGHT,

        TOP_LEFT_RIGHT,
        BOTTOM_LEFT_RIGHT,

        BOTTOM_LEFT,
        BOTTOM_RIGHT,

        TOP_LEFT_BOTTOM,
        TOP_RIGHT_BOTTOM,

        CENTER
    }

    val centerPoint: Pair<Float, Float>
        get() {
            return (x + (radius / 2)) to (y + (radius / 2))
        }

 /*   fun computeShadow(shapes: List<Shape>) {
        shapes.forEach { it.shadow?.computeShift(it, computePosition(it)) }
    }

    fun computeShadow(vararg shapes: Shape) {
        shapes.forEach { it.shadow?.computeShift(it, computePosition(it)) }
    }

    fun computeShadow(shape: Shape) {
        shape.shadow?.computeShift(shape, computePosition(shape))
    }

    fun computeShadow(shape: Shape, shadowPosition: Position?) {
        when (shadowPosition) {
            Position.TOP_LEFT -> shape.shadow?.computeShift(shape, Position.BOTTOM_RIGHT)
            Position.TOP_RIGHT -> shape.shadow?.computeShift(shape, Position.BOTTOM_LEFT)
            Position.TOP_LEFT_RIGHT -> shape.shadow?.computeShift(shape, Position.BOTTOM_LEFT_RIGHT)
            Position.BOTTOM_LEFT_RIGHT -> shape.shadow?.computeShift(shape, Position.TOP_LEFT_RIGHT)
            Position.BOTTOM_LEFT -> shape.shadow?.computeShift(shape, Position.TOP_RIGHT)
            Position.BOTTOM_RIGHT -> shape.shadow?.computeShift(shape, Position.TOP_LEFT)
            Position.TOP_LEFT_BOTTOM -> shape.shadow?.computeShift(shape, Position.TOP_RIGHT_BOTTOM)
            Position.TOP_RIGHT_BOTTOM -> shape.shadow?.computeShift(shape, Position.TOP_LEFT_BOTTOM)
            Position.CENTER -> shape.shadow?.computeShift(shape, Position.CENTER)
            else -> {
                shape.shadow?.computeShift(shape, computePosition(shape))
            }
        }
    }*/

    private fun computePosition(shape: Shape): Position {
        val centerX = shape.coordinate.x + (shape.dimension.width / 2)
        val centerY = shape.coordinate.y + (shape.dimension.height / 2)

        return when {
            centerPoint.first > centerX && centerPoint.second > centerY -> Position.TOP_LEFT
            centerPoint.first < centerX && centerPoint.second > centerY -> Position.TOP_RIGHT

            centerPoint.first > centerX && centerPoint.second < centerY -> Position.BOTTOM_LEFT
            centerPoint.first < centerX && centerPoint.second < centerY -> Position.BOTTOM_RIGHT

            centerPoint.first > centerX && centerPoint.second == centerY -> Position.TOP_RIGHT_BOTTOM
            centerPoint.first < centerX && centerPoint.second == centerY -> Position.TOP_LEFT_BOTTOM

            centerPoint.first == centerX && centerPoint.second > centerY -> Position.BOTTOM_LEFT_RIGHT
            centerPoint.first == centerX && centerPoint.second < centerY -> Position.TOP_LEFT_RIGHT

            centerPoint.first == centerX && centerPoint.second == centerY -> Position.CENTER

            else -> Position.CENTER
        }
    }
}