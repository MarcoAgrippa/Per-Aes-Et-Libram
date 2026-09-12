package com.peraeslibram.domain.model

import java.time.LocalDateTime

/** Objedinjena stavka za Dashboard agendu — ročište ili rok, sortirani po istoj vremenskoj osi. */
sealed interface AgendaItem {
    val dateTime: LocalDateTime
    val caseId: Long

    data class HearingItem(val hearing: Hearing, val caseNaziv: String?) : AgendaItem {
        override val dateTime: LocalDateTime = hearing.datumVreme
        override val caseId: Long = hearing.caseId
    }

    data class DeadlineItem(val deadline: Deadline, val caseNaziv: String?) : AgendaItem {
        override val dateTime: LocalDateTime = deadline.izracunatiKrajnjiDatum.atStartOfDay()
        override val caseId: Long = deadline.caseId
    }
}
