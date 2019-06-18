package com.eudycontreras.bowlingcalculator.persistance.primitive

/**
 * Created by eudycontreras.
 */

interface PrimitiveStorage {

    var userName: String?

    var autoSave: Boolean

    var activeTab: Int

    var currentBowlerIds: LongArray

    fun restoreUserDefaults()
}
