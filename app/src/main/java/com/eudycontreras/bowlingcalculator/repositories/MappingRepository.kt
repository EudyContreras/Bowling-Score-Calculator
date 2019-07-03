package com.eudycontreras.bowlingcalculator.repositories

import com.eudycontreras.bowlingcalculator.persistance.entities.MappingEntity

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

interface MappingRepository {

    suspend fun createMapping(resultId: Long, bowlerId: Long)

    suspend fun getMappings(resultId: Long): List<MappingEntity>

    suspend fun removeMapping(resultId: Long, bowlerId: Long)

    suspend fun removeMappings(resultId: Long)

    suspend fun deleteAll()

}