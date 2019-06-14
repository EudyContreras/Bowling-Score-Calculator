package com.eudycontreras.bowlingcalculator.persistance

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.eudycontreras.bowlingcalculator.adapters.FrameTypeAdapter
import com.eudycontreras.bowlingcalculator.calculator.elements.Bowler
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.google.gson.Gson
import com.google.gson.GsonBuilder



/**
 * Created by eudycontreras.
 */

class SharedPreferencesStorage(context: Context) : PrimitiveStorage {

    companion object {
        private const val ACTIVE_USER = "active_user"
        private const val ACTIVE_USER_DEFAULT = ""

        private const val AUTO_SAVE = "auto_save"
        private const val AUTO_SAVE_DEFAULT = false

        private const val BOWLER = "auto_save"
        private val BOWLER_DEFAULT = null
    }

    private var gson: Gson = Gson()

    init {
        gson = GsonBuilder()
            .registerTypeAdapter(Frame::class.java, FrameTypeAdapter())
            .create()
    }
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var userName: String?
        get() = sharedPreferences.getString(ACTIVE_USER, ACTIVE_USER_DEFAULT)
        set(value) = sharedPreferences.edit { putString(ACTIVE_USER, value) }

    override var autoSave: Boolean
        get() = sharedPreferences.getBoolean(AUTO_SAVE, AUTO_SAVE_DEFAULT)
        set(value) = sharedPreferences.edit { putBoolean(AUTO_SAVE, value) }

    override var bowler: Bowler
        get() = if (sharedPreferences.getString(BOWLER, BOWLER_DEFAULT) == null) {
            Bowler()
        } else {
            gson.fromJson(sharedPreferences.getString(BOWLER, BOWLER_DEFAULT)!!, Bowler::class.java)
        }
        set(value) = sharedPreferences.edit { putString(BOWLER, gson.toJson(value, Bowler::class.java)) }

    override fun restoreUserDefaults() {
        sharedPreferences.edit {
            putString(ACTIVE_USER, ACTIVE_USER_DEFAULT)
            putBoolean(AUTO_SAVE, AUTO_SAVE_DEFAULT)
            putString(BOWLER, BOWLER_DEFAULT)
        }
    }
}
