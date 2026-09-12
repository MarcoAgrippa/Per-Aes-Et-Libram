package com.peraeslibram.data.local.seed

import com.peraeslibram.domain.model.NonWorkingDay
import com.peraeslibram.domain.model.NonWorkingDaySource
import com.peraeslibram.domain.model.NonWorkingDayType
import java.time.LocalDate

/**
 * Generiše listu neradnih dana (državni + verski praznici koje sudovi u Srbiji ne rade)
 * za datu godinu. Fiksni datumi su hardkodirani (ne menjaju se), a pokretni verski
 * praznici (Veliki petak, Vaskrs, Vaskršnji ponedeljak) se računaju preko
 * [OrthodoxEasterCalculator] jer se pomeraju svake godine.
 *
 * Ovo je auto-seed izvor — korisnik može dopuniti/ispraviti listu ručno kroz ekran za
 * praznike (npr. ako se u budućnosti zakonom doda novi praznik).
 */
object HolidaySeedData {

    fun forYear(year: Int): List<NonWorkingDay> {
        val fixed = listOf(
            LocalDate.of(year, 1, 1) to "Nova godina",
            LocalDate.of(year, 1, 2) to "Nova godina (drugi dan)",
            LocalDate.of(year, 1, 7) to "Božić",
            LocalDate.of(year, 2, 15) to "Dan državnosti Srbije",
            LocalDate.of(year, 2, 16) to "Dan državnosti Srbije (drugi dan)",
            LocalDate.of(year, 5, 1) to "Praznik rada",
            LocalDate.of(year, 5, 2) to "Praznik rada (drugi dan)",
            LocalDate.of(year, 11, 11) to "Dan primirja u Prvom svetskom ratu"
        ).map { (datum, naziv) ->
            NonWorkingDay(
                datum = datum,
                naziv = naziv,
                tip = NonWorkingDayType.FIKSNI,
                izvor = NonWorkingDaySource.AUTO_SEED
            )
        }

        val movable = listOf(
            OrthodoxEasterCalculator.greatFriday(year) to "Veliki petak",
            OrthodoxEasterCalculator.orthodoxEasterSunday(year) to "Vaskrs",
            OrthodoxEasterCalculator.easterMonday(year) to "Vaskršnji ponedeljak"
        ).map { (datum, naziv) ->
            NonWorkingDay(
                datum = datum,
                naziv = naziv,
                tip = NonWorkingDayType.POKRETNI,
                izvor = NonWorkingDaySource.AUTO_SEED
            )
        }

        return fixed + movable
    }
}
