package com.peraeslibram.domain.model

/**
 * Referentno (seed) pravilo za automatski obračun roka po kombinaciji
 * (tipPostupka, tipRadnje). [brojDana] je null samo za [TipRadnje.CUSTOM_GENERICKI],
 * gde broj dana unosi korisnik ručno.
 */
data class DeadlineRule(
    val tipPostupka: TipPostupka?,
    val tipRadnje: TipRadnje,
    val brojDana: Int?,
    val nazivPrikaz: String,
    val napomenaPravno: String? = null,
    val aktivno: Boolean = true
)
