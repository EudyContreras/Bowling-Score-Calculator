package com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.shapes.RectShape

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
class MorphShape: RectShape() {
    override fun draw(canvas: Canvas?, paint: Paint?) {
        super.draw(canvas, paint)
    }

    override fun onResize(width: Float, height: Float) {
        super.onResize(width, height)
    }

    override fun getOutline(outline: Outline) {
        super.getOutline(outline)
    }
}