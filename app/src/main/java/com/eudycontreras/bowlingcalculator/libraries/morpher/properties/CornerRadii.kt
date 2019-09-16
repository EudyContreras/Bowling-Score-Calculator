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
class CornerRadii(
    topLeft: Float = 0f,
    topRight: Float = 0f,
    bottomRight: Float = 0f,
    bottomLeft: Float = 0f
) {
    val topLeft: Float
        get() = corners[0]

    val topRight: Float
        get() = corners[2]

    val bottomRight: Float
        get() = corners[4]

    val bottomLeft: Float
        get() = corners[6]

    val corners = FloatArray(8)

    val size: Int
        get() = corners.size

    init {
        apply(topLeft, topRight, bottomRight, bottomLeft)
    }

    constructor(radii: FloatArray): this(radii[0], radii[2], radii[4], radii[6])

    constructor(): this(0f, 0f, 0f, 0f)

    fun apply(cornerRadii: CornerRadii) {
        for (index in 0 until cornerRadii.size) {
            corners[index] = cornerRadii[index]
        }
    }

    fun apply(cornerRadii: FloatArray) {
        for (index in 0 until cornerRadii.size) {
            corners[index] = cornerRadii[index]
        }
    }

    fun apply(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float) {
        corners[0] = topLeft
        corners[1] = topLeft

        corners[2] = topRight
        corners[3] = topRight

        corners[4] = bottomRight
        corners[5] = bottomRight

        corners[6] = bottomLeft
        corners[7] = bottomLeft
    }

    fun getCopy(): CornerRadii {
        return CornerRadii(topLeft, topRight, bottomRight, bottomLeft)
    }

    operator fun get(index: Int): Float {
        return corners[index]
    }

    operator fun set(index: Int, value: Float) {
        corners[index] = value
    }

    override fun toString(): String {
        return "TL: ${corners[0]} TR: ${corners[2]} BR: ${corners[4]} BL: ${corners[6]}"
    }
}