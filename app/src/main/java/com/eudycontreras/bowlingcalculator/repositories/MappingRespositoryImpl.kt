package com.eudycontreras.bowlingcalculator.repositories

import androidx.annotation.WorkerThread
import com.eudycontreras.bowlingcalculator.persistance.PersistenceManager
import com.eudycontreras.bowlingcalculator.persistance.dao.MappingsDao
import com.eudycontreras.bowlingcalculator.persistance.entities.MappingEntity

/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class MappingRespositoryImpl(
    private val manager: PersistenceManager,
    private val mappingsDao: MappingsDao
) : MappingRepository {

    @WorkerThread
    override suspend fun createMapping(resultId: Long, bowlerId: Long) {
        mappingsDao.insert(MappingEntity(resultId, bowlerId))
    }

    @WorkerThread
    override suspend fun getMappings(resultId: Long): List<MappingEntity> {
        return mappingsDao.getForResult(resultId)
    }

    @WorkerThread
    override suspend fun removeMapping(resultId: Long, bowlerId: Long) {
        mappingsDao.delete(resultId, bowlerId)
    }

    @WorkerThread
    override suspend fun removeMappings(resultId: Long) {
        mappingsDao.deleteForResult(resultId)
    }

    @WorkerThread
    override suspend fun deleteAll() {
        mappingsDao.clear()
    }
}