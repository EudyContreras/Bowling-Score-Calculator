package com.eudycontreras.chartasticlibrary.utilities.extensions

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.bowlingcalculator.utilities.properties.CornerRadii

/**
 * Created by eudycontreras.
 */

fun Canvas.drawRoundRect(
    path: Path,

    left: Float,
    top: Float,
    right: Float,
    bottom: Float,

    corners: CornerRadii,
    paint: Paint
) {
    this.drawRoundRect(
        path,
        left,
        top,
        right,
        bottom,
        corners.values,
        paint
    )
}

fun Canvas.drawRoundRect(
    path: Path,

    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    radiusX: Float,
    radiusY: Float,
    paint: Paint
) {
    path.rewind()
    path.addRoundRect(left, top, right, bottom, radiusX, radiusY, Path.Direction.CCW)
    this.drawPath(path, paint)
}

fun Canvas.drawRoundRect(
    path: Path,

    left: Float,
    top: Float,
    right: Float,
    bottom: Float,

    radiusX: Float,
    radiusY: Float,

    corners: CornerRadii,
    paint: Paint
) {
    corners.rx = radiusX
    corners.ry = radiusY
    this.drawRoundRect(
        path,
        left,
        top,
        right,
        bottom,
        corners,
        paint
    )
}
fun Canvas.drawRoundRect(
    path: Path,

    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    corners: FloatArray,
    paint: Paint
) {
    path.rewind()
    path.addRoundRect(left, top, right, bottom, corners, Path.Direction.CCW)
    this.drawPath(path, paint)
}