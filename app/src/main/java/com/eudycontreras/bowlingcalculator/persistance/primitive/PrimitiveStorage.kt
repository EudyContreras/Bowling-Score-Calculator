package com.eudycontreras.bowlingcalculator.persistance.primitive

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

interface PrimitiveStorage {

    var userName: String?

    var autoSave: Boolean

    var activeTab: Int

    var activeTheme: Int

    var currentBowlerIds: LongArray

    fun restoreUserDefaults()
}
