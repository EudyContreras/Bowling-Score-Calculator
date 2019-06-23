package com.eudycontreras.bowlingcalculator.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class SquareFrameLayout : FrameLayout {

    constructor(context: Context):super(context)
    constructor(context: Context, attrs: AttributeSet?):super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }
}