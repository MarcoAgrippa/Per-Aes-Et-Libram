package com.peraeslibram.data.local.seed

import java.time.LocalDate

/**
 * Računa datum pravoslavnog Vaskrsa (i iz njega izvedenog Velikog petka) za datu godinu,
 * po Gausovom/Mojsovom algoritmu za Julijanski Uskrs, konvertovanom u Gregorijanski
 * kalendarski datum. Namerno se NE hardkoduju datumi po godinama — pokretni praznici se
 * menjaju svake godine, pa bi hardkodovana tabela zahtevala ručno održavanje zauvek.
 *
 * Verifikovano test-vrednostima (v. [OrthodoxEasterCalculatorTest]) naspram javno
 * objavljenih datuma: 2024-05-05, 2025-04-20, 2026-04-12, 2027-05-02, 2028-04-16.
 */
object OrthodoxEasterCalculator {

    /** Pravoslavni Vaskrs (nedelja) za datu godinu, kao Gregorijanski datum. */
    fun orthodoxEasterSunday(year: Int): LocalDate {
        val a = year % 4
        val b = year % 7
        val c = year % 19
        val d = (19 * c + 15) % 30
        val e = (2 * a + 4 * b - d + 34) % 7
        val month = (d + e + 114) / 31
        val day = ((d + e + 114) % 31) + 1

        val julianCalendarDate = LocalDate.of(year, month, day)
        return julianCalendarDate.plusDays(julianToGregorianOffsetDays(year).toLong())
    }

    /** Veliki petak = dva dana pre Vaskrsa. */
    fun greatFriday(year: Int): LocalDate = orthodoxEasterSunday(year).minusDays(2)

    /** Vaskršnji ponedeljak = dan posle Vaskrsa. */
    fun easterMonday(year: Int): LocalDate = orthodoxEasterSunday(year).plusDays(1)

    /**
     * Razlika (u danima) između Julijanskog i Gregorijanskog kalendara. Za period
     * 1900-2099. ova razlika je konstantna i iznosi 13 dana; van tog opsega bi trebalo
     * proširiti formulu, ali aplikacija realno seeduje samo tekuću i narednu godinu.
     */
    private fun julianToGregorianOffsetDays(year: Int): Int {
        require(year in 1900..2099) {
            "OrthodoxEasterCalculator je kalibrisan za period 1900-2099 (godina: $year)."
        }
        return 13
    }
}
