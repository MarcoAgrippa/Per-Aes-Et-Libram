package com.peraeslibram.data.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.peraeslibram.R
import com.peraeslibram.app.MainActivity
import com.peraeslibram.data.local.dao.CaseDao
import com.peraeslibram.data.local.dao.DeadlineDao
import com.peraeslibram.data.local.dao.HearingDao
import com.peraeslibram.data.local.dao.ReminderDao
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

/**
 * Prima okidaj lokalnog alarma, dohvata svež snimak roka/ročišta i predmeta iz Room-a
 * (namerno se ne denormalizuje u [Intent] extras — extras imaju ograničenje veličine i
 * podaci bi mogli zastareti između zakazivanja i okidanja), prikazuje proširenu notifikaciju
 * sa linkom ka predmetu, i asinhrono (uz [goAsync]) obeležava odgovarajući
 * [com.peraeslibram.data.local.entity.ReminderEntity] kao poslat, da [BootRescheduleWorker]
 * ne bi ponovo zakazao već okinut podsetnik.
 */
@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var reminderDao: ReminderDao
    @Inject lateinit var hearingDao: HearingDao
    @Inject lateinit var deadlineDao: DeadlineDao
    @Inject lateinit var caseDao: CaseDao

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(AndroidAlarmScheduler.EXTRA_REMINDER_ID, -1L)
        val title = intent.getStringExtra(AndroidAlarmScheduler.EXTRA_TITLE)
        val message = intent.getStringExtra(AndroidAlarmScheduler.EXTRA_MESSAGE)
        val channelId = intent.getStringExtra(AndroidAlarmScheduler.EXTRA_CHANNEL_ID)

        if (reminderId == -1L || title == null || channelId == null) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminder = reminderDao.getById(reminderId)
                val (bigText, caseId) = buildDetails(reminder?.hearingId, reminder?.deadlineId)
                showNotification(context, reminderId.toInt(), title, message.orEmpty(), bigText, caseId, channelId)
                reminderDao.markSent(reminderId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    /** Vraća (prošireni tekst za BigTextStyle, id predmeta za deep link) — oba mogu biti null ako je rok/ročište u međuvremenu obrisano. */
    private suspend fun buildDetails(hearingId: Long?, deadlineId: Long?): Pair<String?, Long?> {
        val hearing = hearingId?.let { hearingDao.getById(it) }
        val deadline = deadlineId?.let { deadlineDao.getById(it) }
        val caseId = hearing?.caseId ?: deadline?.caseId ?: return null to null
        val case = caseDao.getById(caseId) ?: return null to caseId

        val lines = mutableListOf<String>()
        listOfNotNull(case.brojPredmeta, case.klijentIme)
            .joinToString(" — ")
            .takeIf { it.isNotBlank() }
            ?.let { lines += it }

        when {
            hearing != null -> {
                lines += hearing.datumVreme.format(dateTimeFormatter) +
                    (hearing.sudnica?.takeIf { it.isNotBlank() }?.let { ", sudnica $it" } ?: "")
                hearing.tipRocista?.takeIf { it.isNotBlank() }?.let { lines += it }
            }
            deadline != null -> {
                lines += "Rok: " + deadline.izracunatiKrajnjiDatum.format(dateFormatter)
                if (deadline.krajnjiDatumPomeren) lines += "(pomeren rok)"
            }
        }

        return lines.joinToString("\n").takeIf { it.isNotBlank() } to caseId
    }

    private fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        bigText: String?,
        caseId: Long?,
        channelId: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val fullText = listOfNotNull(message.takeIf { it.isNotBlank() }, bigText)
            .joinToString("\n")
        if (fullText.isNotBlank()) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(fullText))
        }

        if (caseId != null) {
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                // Aplikacija je jedna Activity (Navigation Compose) — SINGLE_TOP osigurava
                // da se, ako je već otvorena, samo pozove onNewIntent (bez gubitka nav back stacka).
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_DEEPLINK_CASE_ID, caseId)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    companion object {
        const val EXTRA_DEEPLINK_CASE_ID = "extra_deeplink_case_id"
    }
}
