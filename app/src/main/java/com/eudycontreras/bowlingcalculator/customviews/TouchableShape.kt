package com.eudycontreras.bowlingcalculator.customviews

import android.view.MotionEvent

/**
 * Created by eudycontreras.
 */
interface TouchableShape {
    fun onTouch(event: MotionEvent, x: Float, y: Float)
    fun onLongPressed(event: MotionEvent, x: Float, y: Float)
}