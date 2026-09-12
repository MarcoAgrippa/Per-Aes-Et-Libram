package com.peraeslibram.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

/** minSdk je 26 (Android O), pa su Notification Channels uvek dostupni — bez version-gate-a. */
object NotificationChannels {

    const val CHANNEL_ROCISTA = "channel_rocista"
    const val CHANNEL_ROKOVI = "channel_rokovi"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ROCISTA, "Ročišta", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Podsetnici za zakazana ročišta"
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ROKOVI, "Rokovi", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Podsetnici za procesne rokove (žalbe, tužbe i sl.)"
            }
        )
    }
}
