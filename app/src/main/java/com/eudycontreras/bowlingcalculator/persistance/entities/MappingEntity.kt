package com.eudycontreras.bowlingcalculator.persistance.entities

import androidx.room.Entity

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

@Entity(
    tableName = "Mappings",
    primaryKeys = ["resultId", "bowlerId"]
)
data class MappingEntity(
    val resultId: Long,
    val bowlerId: Long
)