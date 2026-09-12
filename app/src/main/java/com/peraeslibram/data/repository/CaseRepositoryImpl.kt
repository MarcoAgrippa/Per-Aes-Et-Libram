package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.CaseDao
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.data.local.entity.toEntity
import com.peraeslibram.data.local.storage.AttachmentFileStore
import com.peraeslibram.domain.model.Case
import com.peraeslibram.domain.repository.CaseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CaseRepositoryImpl @Inject constructor(
    private val caseDao: CaseDao,
    private val attachmentFileStore: AttachmentFileStore
) : CaseRepository {

    override fun observeAll(): Flow<List<Case>> =
        caseDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Case?> =
        caseDao.observeById(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): Case? = caseDao.getById(id)?.toDomain()

    override suspend fun save(case: Case): Long {
        val entity = case.toEntity()
        return if (entity.id == 0L) caseDao.insert(entity) else {
            caseDao.update(entity)
            entity.id
        }
    }

    override suspend fun delete(case: Case) {
        // DB CASCADE briše redove u `prilozi` automatski, ali ne i fajlove na disku —
        // isti obrazac kao HearingRepositoryImpl (side-effect pre DB brisanja).
        attachmentFileStore.deleteCaseDirectory(case.id)
        caseDao.delete(case.toEntity())
    }
}
