package com.eudycontreras.bowlingcalculator.persistance.primitive

import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler

/**
 * Created by eudycontreras.
 */

interface PrimitiveStorage {

    var userName: String?

    var bowler: Bowler

    var autoSave: Boolean

    var hasBowler: Boolean

    var activeTab: Int

    fun restoreUserDefaults()
}
