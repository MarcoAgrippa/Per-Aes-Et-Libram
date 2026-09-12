package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.PrilogDao
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.data.local.entity.toEntity
import com.peraeslibram.data.local.storage.AttachmentFileStore
import com.peraeslibram.domain.model.Prilog
import com.peraeslibram.domain.repository.PrilogRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrilogRepositoryImpl @Inject constructor(
    private val prilogDao: PrilogDao,
    private val attachmentFileStore: AttachmentFileStore
) : PrilogRepository {

    override fun observeByCase(caseId: Long): Flow<List<Prilog>> =
        prilogDao.observeByCase(caseId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Prilog? = prilogDao.getById(id)?.toDomain()

    override suspend fun save(prilog: Prilog): Long {
        val entity = prilog.toEntity()
        return if (entity.id == 0L) prilogDao.insert(entity) else {
            prilogDao.update(entity)
            entity.id
        }
    }

    override suspend fun rename(prilog: Prilog, newNaziv: String) {
        prilogDao.update(prilog.toEntity().copy(naziv = newNaziv))
    }

    override suspend fun delete(prilog: Prilog) {
        // Isti obrazac kao HearingRepositoryImpl: prvo očisti sporedni efekat van baze
        // (fajl na disku), tek onda ukloni DB red.
        attachmentFileStore.delete(prilog.caseId, prilog.fileName)
        prilogDao.delete(prilog.toEntity())
    }
}
