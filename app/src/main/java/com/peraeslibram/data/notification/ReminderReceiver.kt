package com.peraeslibram.data.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.peraeslibram.R
import com.peraeslibram.data.local.dao.ReminderDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Prima okidaj lokalnog alarma, odmah prikazuje notifikaciju, i asinhrono (uz [goAsync])
 * obeležava odgovarajući [com.peraeslibram.data.local.entity.ReminderEntity] kao poslat,
 * da [BootRescheduleWorker] ne bi ponovo zakazao već okinut podsetnik.
 */
@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderDao: ReminderDao

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(AndroidAlarmScheduler.EXTRA_REMINDER_ID, -1L)
        val title = intent.getStringExtra(AndroidAlarmScheduler.EXTRA_TITLE)
        val message = intent.getStringExtra(AndroidAlarmScheduler.EXTRA_MESSAGE)
        val channelId = intent.getStringExtra(AndroidAlarmScheduler.EXTRA_CHANNEL_ID)

        if (title != null && channelId != null) {
            showNotification(context, reminderId.toInt(), title, message.orEmpty(), channelId)
        }

        if (reminderId != -1L) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    reminderDao.markSent(reminderId)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private fun showNotification(context: Context, notificationId: Int, title: String, message: String, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
