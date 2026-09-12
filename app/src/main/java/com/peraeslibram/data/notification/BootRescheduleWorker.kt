package com.peraeslibram.data.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.peraeslibram.data.local.dao.CaseDao
import com.peraeslibram.data.local.dao.DeadlineDao
import com.peraeslibram.data.local.dao.HearingDao
import com.peraeslibram.data.local.dao.ReminderDao
import com.peraeslibram.data.local.entity.ReminderEntity
import com.peraeslibram.data.local.entity.toDomain
import com.peraeslibram.domain.scheduler.AlarmScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration
import java.time.Instant

/**
 * Ponovo registruje sve aktivne, još-ne-poslate podsetnike posle restarta uređaja
 * (AlarmManager alarmi ne prežive reboot). Podsetnici čije je vreme prošlo dok je
 * uređaj bio ugašen duže od [GRACE_WINDOW] se obeležavaju kao poslati bez prikaza,
 * umesto da se okinu sa velikim zakašnjenjem.
 */
@HiltWorker
class BootRescheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val reminderDao: ReminderDao,
    private val hearingDao: HearingDao,
    private val deadlineDao: DeadlineDao,
    private val caseDao: CaseDao,
    private val alarmScheduler: AlarmScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val now = Instant.now()
        val graceWindowStart = now.minus(GRACE_WINDOW)

        reminderDao.getPending().forEach { entity ->
            if (entity.vremeOkidanja.isBefore(graceWindowStart)) {
                reminderDao.markSent(entity.id)
            } else {
                buildContent(entity)?.let { (title, message, channelId) ->
                    alarmScheduler.schedule(entity.toDomain(), title, message, channelId)
                }
            }
        }
        return Result.success()
    }

    private suspend fun buildContent(entity: ReminderEntity): Triple<String, String, String>? {
        return when {
            entity.hearingId != null -> {
                val hearing = hearingDao.getById(entity.hearingId) ?: return null
                val case = caseDao.getById(hearing.caseId)
                Triple(
                    "Podsetnik: ročište",
                    listOfNotNull(case?.naziv, hearing.sud).joinToString(" — "),
                    NotificationChannels.CHANNEL_ROCISTA
                )
            }
            entity.deadlineId != null -> {
                val deadline = deadlineDao.getById(entity.deadlineId) ?: return null
                val case = caseDao.getById(deadline.caseId)
                Triple(
                    "Podsetnik: rok",
                    listOfNotNull(case?.naziv, deadline.nazivRadnjePrikaz).joinToString(" — "),
                    NotificationChannels.CHANNEL_ROKOVI
                )
            }
            else -> null
        }
    }

    private companion object {
        val GRACE_WINDOW: Duration = Duration.ofHours(24)
    }
}
