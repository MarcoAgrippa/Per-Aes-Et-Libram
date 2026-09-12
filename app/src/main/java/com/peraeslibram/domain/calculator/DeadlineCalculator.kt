package com.peraeslibram.domain.calculator

import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * Rezultat obračuna procesnog roka.
 *
 * @property rawDueDate krajnji datum pre primene pravila o pomeranju (triggerDate + brojDana)
 * @property finalDueDate konačni krajnji datum, pomeren na prvi naredni radni dan ako je [rawDueDate]
 *   padao na vikend ili neradni dan
 * @property wasAdjusted true ako je [finalDueDate] različit od [rawDueDate]
 * @property holidayDataMissingForYears godine (obuhvaćene rasponom [rawDueDate]..[finalDueDate]) za koje
 *   pozivalac nije prosledio potvrđene podatke o praznicima — obračun se svejedno vraća (vikend-korekcija
 *   ne zavisi od tabele praznika), ali UI treba da upozori korisnika da rezultat može biti nepotpun.
 */
data class DeadlineCalculationResult(
    val rawDueDate: LocalDate,
    val finalDueDate: LocalDate,
    val wasAdjusted: Boolean,
    val holidayDataMissingForYears: Set<Int>
)

/**
 * Čist domain servis za obračun procesnih rokova, bez ijedne Android/Room zavisnosti.
 *
 * Pravilo (zajedničko za ZPP/ZKP/ZUP/Zakon o prekršajima): dan dostave/dešavanja se
 * ne računa u rok, rok počinje od narednog dana i traje [brojDana] kalendarskih dana —
 * što je matematički ekvivalentno `triggerDate.plusDays(brojDana)`. Ako poslednji dan
 * roka padne na subotu, nedelju ili državni praznik, rok ističe protekom prvog narednog
 * radnog dana (petlja, jer postoje višednevni praznici poput 1-2. januara).
 */
class DeadlineCalculator @Inject constructor() {

    fun calculateDueDate(
        triggerDate: LocalDate,
        brojDana: Int,
        nonWorkingDays: Set<LocalDate> = emptySet(),
        yearsWithHolidayData: Set<Int> = emptySet()
    ): DeadlineCalculationResult {
        require(brojDana > 0) { "Broj dana roka mora biti pozitivan." }

        val rawDueDate = triggerDate.plusDays(brojDana.toLong())

        var finalDueDate = rawDueDate
        while (isNonWorkingDay(finalDueDate, nonWorkingDays)) {
            finalDueDate = finalDueDate.plusDays(1)
        }

        val missingYears = (rawDueDate.year..finalDueDate.year)
            .filterNot { it in yearsWithHolidayData }
            .toSet()

        return DeadlineCalculationResult(
            rawDueDate = rawDueDate,
            finalDueDate = finalDueDate,
            wasAdjusted = finalDueDate != rawDueDate,
            holidayDataMissingForYears = missingYears
        )
    }

    private fun isNonWorkingDay(date: LocalDate, nonWorkingDays: Set<LocalDate>): Boolean =
        date.dayOfWeek == DayOfWeek.SATURDAY ||
            date.dayOfWeek == DayOfWeek.SUNDAY ||
            date in nonWorkingDays
}
