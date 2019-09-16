package com.eudycontreras.bowlingcalculator.repositories

import com.eudycontreras.bowlingcalculator.persistance.entities.MappingEntity

/**
 * Copyright (C) 2019 Bowling Score Calculator Project
 * Licensed under the MIT license.
 *
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 * @since January 2019
 */
interface MappingRepository {

    suspend fun createMapping(resultId: Long, bowlerId: Long)

    suspend fun getMappings(resultId: Long): List<MappingEntity>

    suspend fun removeMapping(resultId: Long, bowlerId: Long)

    suspend fun removeMappings(resultId: Long)

    suspend fun deleteAll()

}