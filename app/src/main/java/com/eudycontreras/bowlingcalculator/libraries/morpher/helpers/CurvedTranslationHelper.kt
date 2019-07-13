package com.eudycontreras.bowlingcalculator.libraries.morpher.helpers

import com.eudycontreras.bowlingcalculator.libraries.morpher.properties.Coordintates
import java.lang.StrictMath.pow
import kotlin.math.round


class CurvedTranslator(
    fromX: Float = 0f,
    toX: Float = 0f,
    fromY: Float = 0f,
    toY: Float = 0f
) {
    private var startPoint: Coordintates = Coordintates(fromX, fromY)
    private var endPoint: Coordintates = Coordintates(toX, toY)

    private var controlPoint: Coordintates = Coordintates.midPoint(startPoint, endPoint)


    fun setStartPoint(start: Coordintates) {
        startPoint = start
    }

    fun setStartPoint(x: Float, y: Float) {
        setStartPoint(Coordintates(x, y))
    }

    fun setEndPoint(end: Coordintates) {
        endPoint = end
    }

    fun setEndPoint(x: Float, y: Float) {
        setEndPoint(Coordintates(x, y))
    }

    fun setControlPoint(control: Coordintates) {
        controlPoint = control
    }

    fun setControlPoint(x: Float, y: Float) {
        setControlPoint(Coordintates(x, y))
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