package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.NotaryDao
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.data.local.entity.toEntity
import com.peraeslibram.data.local.seed.NotarySeedData
import com.peraeslibram.domain.model.Notary
import com.peraeslibram.domain.repository.NotaryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotaryRepositoryImpl @Inject constructor(
    private val notaryDao: NotaryDao
) : NotaryRepository {

    override fun observeAll(): Flow<List<Notary>> =
        notaryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Notary? = notaryDao.getById(id)?.toDomain()

    override suspend fun save(notary: Notary): Long {
        val entity = notary.toEntity()
        return if (entity.id == 0L) notaryDao.insert(entity) else {
            notaryDao.update(entity)
            entity.id
        }
    }

    override suspend fun delete(notary: Notary) = notaryDao.delete(notary.toEntity())

    override suspend fun ensureSeeded() {
        if (notaryDao.count() == 0) {
            notaryDao.insertAll(NotarySeedData.defaultNotaries().map { it.toEntity() })
        }
    }
}
