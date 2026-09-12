package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.ReminderDao
import com.peraeslibram.data.local.entity.ReminderEntity
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.domain.scheduler.AlarmScheduler
import java.time.Instant
import javax.inject.Inject

/**
 * Deljena logika zakazivanja podsetnika, korišćena od strane [HearingRepositoryImpl] i
 * [DeadlineRepositoryImpl]. Garantuje invarijantu: postojeći alarmi se UVEK otkazuju
 * ([cancelAndClear]) pre nego što se kreiraju i zakažu novi ([scheduleAll]) — pozivalac
 * (repository) je odgovoran da ova dva poziva idu tim redosledom.
 */
class ReminderCoordinator @Inject constructor(
    private val reminderDao: ReminderDao,
    private val alarmScheduler: AlarmScheduler
) {

    suspend fun cancelAndClear(hearingId: Long? = null, deadlineId: Long? = null) {
        val existing = when {
            hearingId != null -> reminderDao.getForHearing(hearingId)
            deadlineId != null -> reminderDao.getForDeadline(deadlineId)
            else -> emptyList()
        }
        existing.forEach { alarmScheduler.cancel(it.toDomain()) }
        if (hearingId != null) reminderDao.deleteForHearing(hearingId)
        if (deadlineId != null) reminderDao.deleteForDeadline(deadlineId)
    }

    suspend fun scheduleAll(
        hearingId: Long?,
        deadlineId: Long?,
        targetInstant: Instant,
        minutesBeforeList: List<Long>,
        title: String,
        message: String,
        channelId: String
    ) {
        minutesBeforeList.forEach { minutesBefore ->
            val vremeOkidanja = targetInstant.minusSeconds(minutesBefore * 60)
            if (vremeOkidanja.isAfter(Instant.now())) {
                val insertedId = reminderDao.insert(
                    ReminderEntity(
                        hearingId = hearingId,
                        deadlineId = deadlineId,
                        minutesBefore = minutesBefore,
                        vremeOkidanja = vremeOkidanja,
                        notifikacijaId = 0,
                        aktivan = true,
                        poslat = false
                    )
                )
                val entity = ReminderEntity(
                    id = insertedId,
                    hearingId = hearingId,
                    deadlineId = deadlineId,
                    minutesBefore = minutesBefore,
                    vremeOkidanja = vremeOkidanja,
                    notifikacijaId = insertedId.toInt(),
                    aktivan = true,
                    poslat = false
                )
                reminderDao.update(entity)
                alarmScheduler.schedule(entity.toDomain(), title, message, channelId)
            }
        }
    }
}
