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
data class Dimension(
    var width: Float = 0f,
    var height: Float = 0f
) {
    var padding = Padding()

    fun copy(): Dimension {
        return Dimension(width, height)
    }
}