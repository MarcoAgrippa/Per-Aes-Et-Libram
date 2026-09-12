package com.peraeslibram.domain.model

import java.time.Instant

data class Case(
    val id: Long = 0,
    val naziv: String,
    val brojPredmeta: String? = null,
    val klijentIme: String,
    val klijentKontakt: String? = null,
    val sud: String? = null,
    val tipPostupka: TipPostupka,
    val napomena: String? = null,
    val status: CaseStatus = CaseStatus.AKTIVAN,
    val datumKreiranja: Instant = Instant.now(),
    val datumIzmene: Instant = datumKreiranja
)
