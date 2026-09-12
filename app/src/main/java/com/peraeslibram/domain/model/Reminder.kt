package com.peraeslibram.domain.model

import java.time.Instant

/**
 * Jedan zakazani lokalni podsetnik. Tačno jedno od [hearingId]/[deadlineId] mora
 * biti postavljeno — Room ne podržava pravi polimorfni FK, pa se ovako dobija
 * referencijalni integritet za oba tipa nadređenog entiteta.
 */
data class Reminder(
    val id: Long = 0,
    val hearingId: Long? = null,
    val deadlineId: Long? = null,
    val minutesBefore: Long,
    val vremeOkidanja: Instant,
    val notifikacijaId: Int,
    val aktivan: Boolean = true,
    val poslat: Boolean = false
) {
    init {
        require((hearingId == null) != (deadlineId == null)) {
            "Reminder mora biti vezan za tačno jedno od: ročište ili rok."
        }
    }
}
