package com.eudycontreras.bowlingcalculator.libraries.morpher.listeners

import android.graphics.Canvas

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
interface DrawDispatchListener {
    fun onDrawDispatched(canvas: Canvas)
    fun onDraw(canvas: Canvas)
}
