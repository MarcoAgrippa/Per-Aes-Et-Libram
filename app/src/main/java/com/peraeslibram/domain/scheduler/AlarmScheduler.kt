package com.peraeslibram.domain.scheduler

import com.peraeslibram.domain.model.Reminder

/**
 * Apstrakcija nad mehanizmom zakazivanja lokalnih podsetnika (u produkciji: AlarmManager).
 * Odvojena kao interfejs da bi se u repository testovima mogla ubaciti lažna (fake)
 * implementacija i proveriti invarijanta "cancel pre reschedule" bez dodirivanja Android API-ja.
 */
interface AlarmScheduler {
    /** Zakazuje tačan alarm za [reminder]. [title]/[message] su sadržaj notifikacije. */
    fun schedule(reminder: Reminder, title: String, message: String, channelId: String)

    /** Otkazuje prethodno zakazan alarm za [reminder] (po [Reminder.notifikacijaId]). */
    fun cancel(reminder: Reminder)
}
