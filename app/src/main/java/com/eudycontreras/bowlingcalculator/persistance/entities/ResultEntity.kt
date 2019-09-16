package com.eudycontreras.bowlingcalculator.persistance.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eudycontreras.bowlingcalculator.calculator.elements.Result
import com.eudycontreras.bowlingcalculator.utilities.NO_ID
import java.util.*

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
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