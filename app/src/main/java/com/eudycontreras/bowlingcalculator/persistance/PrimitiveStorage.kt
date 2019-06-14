package com.eudycontreras.bowlingcalculator.persistance

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler

/**
 * Created by eudycontreras.
 */

interface PrimitiveStorage {

    var userName: String?

    var bowler: Bowler

    var autoSave: Boolean

    fun restoreUserDefaults()
}
