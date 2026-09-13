package com.peraeslibram.domain.model

import java.time.Instant

data class Notary(
    val id: Long = 0,
    val naziv: String,
    val adresa: String? = null,
    val telefon: String? = null,
    val email: String? = null,
    val napomena: String? = null,
    val datumKreiranja: Instant = Instant.now(),
    val datumIzmene: Instant = datumKreiranja
)
