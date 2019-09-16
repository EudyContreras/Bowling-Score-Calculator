package com.eudycontreras.bowlingcalculator.persistance.entities

import androidx.room.Entity

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */

@Entity(
    tableName = "Mappings",
    primaryKeys = ["resultId", "bowlerId"]
)
data class MappingEntity(
    val resultId: Long,
    val bowlerId: Long
)