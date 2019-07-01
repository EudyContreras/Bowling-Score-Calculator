package com.eudycontreras.chartasticlibrary.utilities.extensions

import android.graphics.Paint

/**
 * Created by eudycontreras.
 */

fun Paint.recycle(){
    shader = null
    pathEffect = null

    reset()
    clearShadowLayer()
}