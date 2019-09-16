package com.eudycontreras.bowlingcalculator.libraries.morpher.properties

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
data class Bounds(
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = 0f,
    var height: Float = 0f
) {
    fun inRange(sourceX: Float, sourceY: Float): Boolean {
        return sourceX >= x && sourceX < width && sourceY >= y && sourceY < height
    }

    fun inRange(sourceX: Float, sourceY: Float, radius: Float): Boolean {
        return sourceX >= x - radius && sourceX < width + radius && sourceY >= y - radius && sourceY < height + radius
    }
}