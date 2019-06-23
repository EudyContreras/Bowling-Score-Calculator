package com.eudycontreras.bowlingcalculator.persistance.primitive

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface PrimitiveStorage {

    var userName: String?

    var autoSave: Boolean

    var activeTab: Int

    var currentBowlerIds: LongArray

    fun restoreUserDefaults()
}
