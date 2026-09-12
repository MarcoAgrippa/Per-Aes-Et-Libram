package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.CaseDao
import com.peraeslibram.data.local.dao.DeadlineDao
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.data.local.entity.toEntity
import com.peraeslibram.data.notification.NotificationChannels
import com.peraeslibram.domain.model.Deadline
import com.peraeslibram.domain.repository.DeadlineRepository
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeadlineRepositoryImpl @Inject constructor(
    private val deadlineDao: DeadlineDao,
    private val caseDao: CaseDao,
    private val reminderCoordinator: ReminderCoordinator
) : DeadlineRepository {

    override fun observeAll(): Flow<List<Deadline>> =
        deadlineDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByCase(caseId: Long): Flow<List<Deadline>> =
        deadlineDao.observeByCase(caseId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Deadline? = deadlineDao.getById(id)?.toDomain()

    override suspend fun save(deadline: Deadline, reminderMinutesBefore: List<Long>): Long {
        val entity = deadline.toEntity()
        val deadlineId = if (entity.id == 0L) deadlineDao.insert(entity) else {
            deadlineDao.update(entity)
            entity.id
        }

        // Invarijanta: prvo otkazati postojeće alarme, tek onda zakazati nove.
        reminderCoordinator.cancelAndClear(deadlineId = deadlineId)

        val saved = deadlineDao.getById(deadlineId) ?: return deadlineId
        val case = caseDao.getById(saved.caseId)
        reminderCoordinator.scheduleAll(
            hearingId = null,
            deadlineId = deadlineId,
            targetInstant = saved.izracunatiKrajnjiDatum.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            minutesBeforeList = reminderMinutesBefore,
            title = "Podsetnik: rok",
            message = listOfNotNull(case?.naziv, saved.nazivRadnjePrikaz).joinToString(" — "),
            channelId = NotificationChannels.CHANNEL_ROKOVI
        )
        return deadlineId
    }

    override suspend fun delete(deadline: Deadline) {
        reminderCoordinator.cancelAndClear(deadlineId = deadline.id)
        deadlineDao.delete(deadline.toEntity())
    }
}
