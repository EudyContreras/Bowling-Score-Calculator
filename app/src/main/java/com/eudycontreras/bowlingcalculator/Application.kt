package com.eudycontreras.bowlingcalculator

import android.app.Application
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
class Application : Application() {

    lateinit var persistenceManager: PersistenceManager

    override fun onCreate() {
        super.onCreate()
        persistenceManager = PersistenceManager(this)
    }
}
