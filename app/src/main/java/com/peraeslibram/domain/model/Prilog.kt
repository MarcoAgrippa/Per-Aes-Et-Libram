package com.peraeslibram.domain.model

import java.time.Instant

data class Prilog(
    val id: Long = 0,
    val caseId: Long,
    val naziv: String,
    val fileName: String,
    val datumKreiranja: Instant = Instant.now()
)
