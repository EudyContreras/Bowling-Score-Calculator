package com.eudycontreras.bowlingcalculator.persistance.primitive

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.eudycontreras.bowlingcalculator.adapters.FrameTypeAdapter
import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Created by eudycontreras.
 */

class PrimitiveStorageImpl(context: Context) :
    PrimitiveStorage {

    companion object {
        private const val ACTIVE_USER = "active_user"
        private const val ACTIVE_USER_DEFAULT = ""

        private const val AUTO_SAVE = "auto_save"
        private const val AUTO_SAVE_DEFAULT = false

        private const val ACTIVE_TAB_INDEX = "active_tab_index"
        private const val ACTIVE_TAB_INDEX_DEFAULT = 0

        private const val BOWLER_IDS = "bowler_ids"
        private val BOWLER_IDS_DEFAULT = null
    }

    private var gson: Gson = Gson()

    init {
        gson = GsonBuilder()
            .registerTypeAdapter(Frame::class.java, FrameTypeAdapter())
            .create()
    }
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var userName: String?
        get() = sharedPreferences.getString(
            ACTIVE_USER,
            ACTIVE_USER_DEFAULT
        )
        set(value) = sharedPreferences.edit { putString(ACTIVE_USER, value) }

    override var autoSave: Boolean
        get() = sharedPreferences.getBoolean(
            AUTO_SAVE,
            AUTO_SAVE_DEFAULT
        )
        set(value) = sharedPreferences.edit { putBoolean(AUTO_SAVE, value) }

    override var activeTab: Int
        get() = sharedPreferences.getInt(
            ACTIVE_TAB_INDEX,
            ACTIVE_TAB_INDEX_DEFAULT
        )
        set(value) = sharedPreferences.edit { putInt(ACTIVE_TAB_INDEX, value) }

    override var currentBowlerIds: LongArray
        get() = if (sharedPreferences.getString(BOWLER_IDS, BOWLER_IDS_DEFAULT) == null) {
            longArrayOf()
        } else {
            gson.fromJson(sharedPreferences.getString(BOWLER_IDS, BOWLER_IDS_DEFAULT)!!, LongArray::class.java)
        }
        set(value) = sharedPreferences.edit { putString(BOWLER_IDS, gson.toJson(value, LongArray::class.java)) }

    override fun restoreUserDefaults() {
        sharedPreferences.edit {
            putString(
                ACTIVE_USER,
                ACTIVE_USER_DEFAULT
            )
            putBoolean(
                AUTO_SAVE,
                AUTO_SAVE_DEFAULT
            )
            putString(
                BOWLER_IDS,
                BOWLER_IDS_DEFAULT
            )
        }
    }
}
