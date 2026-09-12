package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.CaseDao
import com.peraeslibram.data.local.dao.HearingDao
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.data.local.entity.toEntity
import com.peraeslibram.data.notification.NotificationChannels
import com.peraeslibram.domain.model.Hearing
import com.peraeslibram.domain.repository.HearingRepository
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HearingRepositoryImpl @Inject constructor(
    private val hearingDao: HearingDao,
    private val caseDao: CaseDao,
    private val reminderCoordinator: ReminderCoordinator
) : HearingRepository {

    override fun observeAll(): Flow<List<Hearing>> =
        hearingDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByCase(caseId: Long): Flow<List<Hearing>> =
        hearingDao.observeByCase(caseId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Hearing? = hearingDao.getById(id)?.toDomain()

    override suspend fun save(hearing: Hearing, reminderMinutesBefore: List<Long>): Long {
        val entity = hearing.toEntity()
        val hearingId = if (entity.id == 0L) hearingDao.insert(entity) else {
            hearingDao.update(entity)
            entity.id
        }

        // Invarijanta: prvo otkazati postojeće alarme, tek onda zakazati nove.
        reminderCoordinator.cancelAndClear(hearingId = hearingId)

        val saved = hearingDao.getById(hearingId) ?: return hearingId
        val case = caseDao.getById(saved.caseId)
        reminderCoordinator.scheduleAll(
            hearingId = hearingId,
            deadlineId = null,
            targetInstant = saved.datumVreme.atZone(ZoneId.systemDefault()).toInstant(),
            minutesBeforeList = reminderMinutesBefore,
            title = "Podsetnik: ročište",
            message = listOfNotNull(case?.naziv, saved.sud).joinToString(" — "),
            channelId = NotificationChannels.CHANNEL_ROCISTA
        )
        return hearingId
    }

    override suspend fun delete(hearing: Hearing) {
        reminderCoordinator.cancelAndClear(hearingId = hearing.id)
        hearingDao.delete(hearing.toEntity())
    }
}
