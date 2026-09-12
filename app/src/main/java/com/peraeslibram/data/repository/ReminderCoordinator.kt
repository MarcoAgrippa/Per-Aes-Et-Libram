package com.peraeslibram.data.repository

import com.peraeslibram.data.local.dao.ReminderDao
import com.peraeslibram.data.local.entity.ReminderEntity
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.di.IoDispatcher
import com.peraeslibram.domain.scheduler.AlarmScheduler
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Deljena logika zakazivanja podsetnika, korišćena od strane [HearingRepositoryImpl] i
 * [DeadlineRepositoryImpl]. Garantuje invarijantu: postojeći alarmi se UVEK otkazuju
 * ([cancelAndClear]) pre nego što se kreiraju i zakažu novi ([scheduleAll]) — pozivalac
 * (repository) je odgovoran da ova dva poziva idu tim redosledom.
 *
 * Oba posla se izvršavaju na [ioDispatcher]. [AlarmScheduler] nije suspendujući, a svaki
 * njegov poziv je sinhrona binder transakcija ka system serveru (`PendingIntent.getBroadcast`,
 * `canScheduleExactAlarms`, `setExactAndAllowWhileIdle`) — bez ovog prebacivanja bi se, posle
 * povratka iz Room-a, ceo niz izvršavao na glavnoj niti i vidljivo odlagao odziv dugmeta.
 */
class ReminderCoordinator @Inject constructor(
    private val reminderDao: ReminderDao,
    private val alarmScheduler: AlarmScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun cancelAndClear(hearingId: Long? = null, deadlineId: Long? = null) {
        withContext(ioDispatcher) {
            val existing = when {
                hearingId != null -> reminderDao.getForHearing(hearingId)
                deadlineId != null -> reminderDao.getForDeadline(deadlineId)
                else -> emptyList()
            }
            existing.forEach { alarmScheduler.cancel(it.toDomain()) }
            if (hearingId != null) reminderDao.deleteForHearing(hearingId)
            if (deadlineId != null) reminderDao.deleteForDeadline(deadlineId)
        }
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
        withContext(ioDispatcher) {
            minutesBeforeList.forEach { minutesBefore ->
                val vremeOkidanja = targetInstant.minusSeconds(minutesBefore * 60)
                if (vremeOkidanja.isAfter(Instant.now())) {
                    val novi = ReminderEntity(
                        hearingId = hearingId,
                        deadlineId = deadlineId,
                        minutesBefore = minutesBefore,
                        vremeOkidanja = vremeOkidanja,
                        notifikacijaId = 0,
                        aktivan = true,
                        poslat = false
                    )
                    // `notifikacijaId` se izvodi iz autogenerisanog id-a, pa je drugi upis neizbežan.
                    val insertedId = reminderDao.insert(novi)
                    val entity = novi.copy(id = insertedId, notifikacijaId = insertedId.toInt())
                    reminderDao.update(entity)
                    alarmScheduler.schedule(entity.toDomain(), title, message, channelId)
                }
            }
        }
    }
}
