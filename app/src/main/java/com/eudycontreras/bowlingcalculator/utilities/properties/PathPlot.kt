package com.eudycontreras.bowlingcalculator.utilities.properties

import android.graphics.Path

/**
 * Created by eudycontreras.
 */
class PathPlot(val path: Path){

    enum class Type{
        QUAD,
        LINE
    }

    val points: ArrayList<PathPoint> = ArrayList()

    var startX: Float = 0f

    var startY: Float = 0f

    var width: Float = 0f

    var height: Float = 0f

    var contentBounds: Bounds = Bounds()

    var pathCreated: Boolean = false

    fun translate(dx: Float, dy: Float) {
        val offsetX = dx - startX
        val offsetY = dy - startY
        path.offset(offsetX, offsetY)
        startX = dx
        startY = dy
    }

    fun build() {
        path.rewind()
        path.moveTo(startX, startY)

        for(point in points) {
            when (point.type) {
                Type.LINE -> {
                    if(point.relative) {
                        path.rLineTo(point.startX, point.startY)
                    }else{
                        path.lineTo(point.startX, point.startY)
                    }
                }
                Type.QUAD -> {
                    if(point.relative) {
                        path.rQuadTo(point.startX, point.startY, point.endX, point.endY)
                    }else{
                        path.quadTo(point.startX, point.startY, point.endX, point.endY)
                    }
                }
            }
        }
        pathCreated = true
        path.close()
    }
}