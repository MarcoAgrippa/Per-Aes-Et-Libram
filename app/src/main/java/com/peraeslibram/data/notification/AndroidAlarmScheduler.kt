package com.peraeslibram.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.peraeslibram.domain.model.Reminder
import com.peraeslibram.domain.scheduler.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Koristi [AlarmManager.setExactAndAllowWhileIdle] — jedina API garancija tačnog vremena
 * čak i u Doze modu. Alarmi ne prežive restart uređaja; za to služi [BootRescheduleWorker].
 */
class AndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(reminder: Reminder, title: String, message: String, channelId: String) {
        val pendingIntent = buildPendingIntent(reminder.notifikacijaId, title, message, channelId)
        val triggerAtMillis = reminder.vremeOkidanja.toEpochMilli()

        val canScheduleExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    override fun cancel(reminder: Reminder) {
        val pendingIntent = buildPendingIntent(reminder.notifikacijaId, title = null, message = null, channelId = null)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun buildPendingIntent(
        notifikacijaId: Int,
        title: String?,
        message: String?,
        channelId: String?
    ): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, notifikacijaId.toLong())
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_CHANNEL_ID, channelId)
        }
        return PendingIntent.getBroadcast(
            context,
            notifikacijaId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"
    }
}
