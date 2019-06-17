package com.eudycontreras.bowlingcalculator.persistance.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eudycontreras.bowlingcalculator.NO_ID
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import java.util.*


/**
 * Created by eudycontreras.
 */

@Entity(tableName = "Results")
data class ResultEntity(
    val name: String,
    val date: Date
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    fun toResult(): Result {
        return Result(name, date).also { it.id = id ?: NO_ID }
    }

    companion object {
        fun from(result: Result): ResultEntity {
            return ResultEntity(
                name = result.name,
                date = result.date
            ).also { it.id = if (result.id == NO_ID) null else result.id }
        }
    }
}