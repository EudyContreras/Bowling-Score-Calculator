package com.eudycontreras.bowlingcalculator.helpers

import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameNormal
import com.google.gson.Gson


/**
 * Created by eudycontreras.
 */

inline fun <reified T: Frame> Gson.getObjectFromString(string: String, type: T): T {
    return if (type is FrameNormal) {
        this.fromJson(string, FrameNormal::class.java) as T
    } else {
        this.fromJson(string, FrameLast::class.java) as T
    }
}

inline fun <reified T: Frame> Gson.makeObjectToString(obj: T): String {
    return if (obj is FrameNormal) {
        this.toJson(obj, FrameNormal::class.java)
    } else {
        this.toJson(obj, FrameLast::class.java)
    }
}