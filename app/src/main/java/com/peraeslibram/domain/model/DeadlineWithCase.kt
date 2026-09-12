package com.peraeslibram.domain.model

import java.time.LocalDate

/** Rok obogaćen nazivom predmeta — za ekran "Rokovi" koji prikazuje rokove svih predmeta. */
data class DeadlineWithCase(
    val deadline: Deadline,
    val caseNaziv: String?
) {
    val datum: LocalDate get() = deadline.izracunatiKrajnjiDatum
}
