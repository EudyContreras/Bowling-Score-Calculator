package com.eudycontreras.bowlingcalculator.libraries.morpher.effectViews

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.shapes.RectShape

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