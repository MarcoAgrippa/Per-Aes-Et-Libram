package com.peraeslibram.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Alarmi zakazani preko [AlarmManager] ne prežive restart uređaja — ovaj receiver
 * pri svakom boot-u pokreće [BootRescheduleWorker] da ponovo registruje sve aktivne podsetnike.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val request = OneTimeWorkRequestBuilder<BootRescheduleWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
