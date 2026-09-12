package com.peraeslibram.domain.model

import java.time.Instant
import java.time.LocalDate

/**
 * Procesni rok vezan za predmet.
 *
 * [brojDana] i [nazivRadnjePrikaz] su namerno snapshot vrednosti kopirane iz
 * [DeadlineRule] u trenutku kreiranja roka — buduća izmena zakonskog pravila
 * (npr. novelacija ZPP-a) ne sme retroaktivno promeniti već obračunate rokove.
 */
data class Deadline(
    val id: Long = 0,
    val caseId: Long,
    val tipPostupka: TipPostupka,
    val tipRadnje: TipRadnje,
    val nazivRadnjePrikaz: String,
    val opisCustomRadnje: String? = null,
    val datumOkidaca: LocalDate,
    val brojDana: Int,
    val izracunatiKrajnjiDatum: LocalDate,
    val originalniKrajnjiDatum: LocalDate? = null,
    val krajnjiDatumPomeren: Boolean = false,
    val status: DeadlineStatus = DeadlineStatus.AKTIVAN,
    val izvor: DataSource = DataSource.MANUELNO,
    val napomena: String? = null,
    val datumKreiranja: Instant = Instant.now(),
    val datumIzmene: Instant = datumKreiranja
) {
    fun isIstekao(danas: LocalDate = LocalDate.now()): Boolean =
        status == DeadlineStatus.AKTIVAN && izracunatiKrajnjiDatum.isBefore(danas)
}
