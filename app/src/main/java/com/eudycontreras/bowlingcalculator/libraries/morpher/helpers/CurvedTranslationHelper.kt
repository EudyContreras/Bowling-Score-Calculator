package com.eudycontreras.bowlingcalculator.libraries.morpher.helpers

import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Coordinates
import java.lang.StrictMath.pow
import kotlin.math.round

/**
 * Copyright (C) 2019 Motion Morpher Project
 *
 **Note:** Unlicensed private property of the author and creator
 * unauthorized use of this class outside of the Motion Morpher project
 * or other projects to which the author has explicitly added this library
 * may result on legal prosecution.
 *
 * @Project Motion Morpher
 * @author Eudy Contreras.
 * @since March 2019
 */
class CurvedTranslationHelper(
    fromX: Float = 0f,
    toX: Float = 0f,
    fromY: Float = 0f,
    toY: Float = 0f
) {
    private var startPoint: Coordinates = Coordinates(fromX, fromY)
    private var endPoint: Coordinates = Coordinates(toX, toY)

    private var controlPoint: Coordinates = Coordinates.midPoint(startPoint, endPoint)


    fun setStartPoint(start: Coordinates) {
        startPoint = start
    }

    fun setStartPoint(x: Float, y: Float) {
        setStartPoint(Coordinates(x, y))
    }

    fun setEndPoint(end: Coordinates) {
        endPoint = end
    }

    fun setEndPoint(x: Float, y: Float) {
        setEndPoint(Coordinates(x, y))
    }

    fun setControlPoint(control: Coordinates) {
        controlPoint = control
    }

    fun setControlPoint(x: Float, y: Float) {
        setControlPoint(Coordinates(x, y))
    }

    fun getCurvedTranslationX(scale: Float): Double {
        return calcBezier(scale.toDouble(), startPoint.x, controlPoint.x, endPoint.x)
    }

    fun getCurvedTranslationY(scale: Float): Double {
        return calcBezier(scale.toDouble(), startPoint.y, controlPoint.y, endPoint.y)
    }

    private fun calcBezier(scale: Double, start: Float, control: Float, end: Float): Double {
        return round(pow(((1.0 - scale)), 2.0) * start) + (2.0 * (1.0 - scale) * scale * control) + (pow(scale, 2.0) * end)
    }
}