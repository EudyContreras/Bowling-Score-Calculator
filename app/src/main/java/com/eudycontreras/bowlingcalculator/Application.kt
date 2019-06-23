package com.eudycontreras.bowlingcalculator

import android.app.Application
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class Application : Application() {

    lateinit var persistenceManager: PersistenceManager

    override fun onCreate() {
        super.onCreate()
        persistenceManager = PersistenceManager(this)
    }
}
