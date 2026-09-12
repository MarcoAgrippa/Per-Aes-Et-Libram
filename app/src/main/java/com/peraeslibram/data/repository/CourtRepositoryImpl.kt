package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.CourtDao
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.data.local.entity.toEntity
import com.peraeslibram.domain.model.Court
import com.peraeslibram.domain.repository.CourtRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourtRepositoryImpl @Inject constructor(
    private val courtDao: CourtDao
) : CourtRepository {

    override fun observeAll(): Flow<List<Court>> =
        courtDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Court? = courtDao.getById(id)?.toDomain()

    override suspend fun save(court: Court): Long {
        val entity = court.toEntity()
        return if (entity.id == 0L) courtDao.insert(entity) else {
            courtDao.update(entity)
            entity.id
        }
    }

    override suspend fun delete(court: Court) = courtDao.delete(court.toEntity())
}
