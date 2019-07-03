package com.eudycontreras.bowlingcalculator.utilities.properties

/**
 * Created by eudycontreras.
 */
data class PathPoint(
    var type: PathPlot.Type,
    var relative: Boolean = true,
    var startX: Float,
    var startY: Float,
    var endX: Float = 0f,
    var endY: Float = 0f
)