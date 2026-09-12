package com.peraeslibram.domain.model

import java.time.Instant
import java.time.LocalDateTime

data class Hearing(
    val id: Long = 0,
    val caseId: Long,
    val datumVreme: LocalDateTime,
    val sud: String? = null,
    val sudnica: String? = null,
    val tipRocista: String? = null,
    val napomena: String? = null,
    val status: HearingStatus = HearingStatus.ZAKAZANO,
    val datumKreiranja: Instant = Instant.now(),
    val datumIzmene: Instant = datumKreiranja
)
