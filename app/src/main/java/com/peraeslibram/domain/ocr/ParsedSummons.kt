package com.peraeslibram.domain.ocr

import java.time.LocalDate
import java.time.LocalTime

/** Rezultat [SummonsParser]-a nad OCR tekstom skeniranog sudskog poziva. */
data class ParsedSummons(
    val datum: LocalDate? = null,
    val vreme: LocalTime? = null,
    val sud: String? = null,
    val sudnica: String? = null,
    val tipRocista: String? = null,
    val brojPredmeta: String? = null,
    val rawText: String = ""
)
