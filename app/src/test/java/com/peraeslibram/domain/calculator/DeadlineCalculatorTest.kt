package com.peraeslibram.domain.calculator

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DeadlineCalculatorTest {

    private val calculator = DeadlineCalculator()

    @Test
    fun `standard 15 day parnicni rok bez pomeranja`() {
        // 2026-02-15 + 15 dana = 2026-03-02, ponedeljak, nije praznik.
        val result = calculator.calculateDueDate(
            triggerDate = LocalDate.of(2026, 2, 15),
            brojDana = 15
        )

        assertEquals(LocalDate.of(2026, 3, 2), result.rawDueDate)
        assertEquals(LocalDate.of(2026, 3, 2), result.finalDueDate)
        assertFalse(result.wasAdjusted)
    }

    @Test
    fun `pomeranje sa subote na ponedeljak`() {
        // 2026-02-27 + 8 dana = 2026-03-07, subota -> pomera se na 2026-03-09, ponedeljak.
        val result = calculator.calculateDueDate(
            triggerDate = LocalDate.of(2026, 2, 27),
            brojDana = 8
        )

        assertEquals(LocalDate.of(2026, 3, 7), result.rawDueDate)
        assertEquals(LocalDate.of(2026, 3, 9), result.finalDueDate)
        assertTrue(result.wasAdjusted)
    }

    @Test
    fun `pomeranje sa nedelje na ponedeljak`() {
        // 2026-02-19 + 10 dana = 2026-03-01, nedelja -> pomera se na 2026-03-02, ponedeljak.
        val result = calculator.calculateDueDate(
            triggerDate = LocalDate.of(2026, 2, 19),
            brojDana = 10
        )

        assertEquals(LocalDate.of(2026, 3, 1), result.rawDueDate)
        assertEquals(LocalDate.of(2026, 3, 2), result.finalDueDate)
        assertTrue(result.wasAdjusted)
    }

    @Test
    fun `kaskadno pomeranje preko visednevnog praznika Nova godina i vikenda`() {
        // 2025-12-17 + 15 dana = 2026-01-01 (cetvrtak, praznik) -> 01.01(praznik) -> 02.01(praznik)
        // -> 03.01(subota) -> 04.01(nedelja) -> konacno 05.01.2026 (ponedeljak).
        val nonWorkingDays = setOf(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2))

        val result = calculator.calculateDueDate(
            triggerDate = LocalDate.of(2025, 12, 17),
            brojDana = 15,
            nonWorkingDays = nonWorkingDays,
            yearsWithHolidayData = setOf(2025, 2026)
        )

        assertEquals(LocalDate.of(2026, 1, 1), result.rawDueDate)
        assertEquals(LocalDate.of(2026, 1, 5), result.finalDueDate)
        assertTrue(result.wasAdjusted)
        assertTrue(result.holidayDataMissingForYears.isEmpty())
    }

    @Test
    fun `menicni cekovni spor 8 dana`() {
        val result = calculator.calculateDueDate(
            triggerDate = LocalDate.of(2026, 2, 15),
            brojDana = 8
        )

        assertEquals(LocalDate.of(2026, 2, 23), result.rawDueDate)
    }

    @Test
    fun `krivicni redovni postupak 15 dana i skraceni 8 dana koriste istu logiku`() {
        val redovni = calculator.calculateDueDate(LocalDate.of(2026, 2, 15), brojDana = 15)
        val skraceni = calculator.calculateDueDate(LocalDate.of(2026, 2, 15), brojDana = 8)

        assertEquals(LocalDate.of(2026, 3, 2), redovni.finalDueDate)
        assertEquals(LocalDate.of(2026, 2, 23), skraceni.finalDueDate)
    }

    @Test
    fun `upravni postupak zalba na resenje 15 dana`() {
        val result = calculator.calculateDueDate(LocalDate.of(2026, 2, 15), brojDana = 15)
        assertEquals(LocalDate.of(2026, 3, 2), result.finalDueDate)
    }

    @Test
    fun `prekrsajni prigovor i zalba 8 dana`() {
        val result = calculator.calculateDueDate(LocalDate.of(2026, 2, 15), brojDana = 8)
        assertEquals(LocalDate.of(2026, 2, 23), result.finalDueDate)
    }

    @Test
    fun `genericki custom rok koristi identican kod put kao ugradjena pravila`() {
        val result = calculator.calculateDueDate(LocalDate.of(2026, 2, 15), brojDana = 30)
        assertEquals(LocalDate.of(2026, 3, 17), result.finalDueDate)
        assertFalse(result.wasAdjusted)
    }

    @Test
    fun `rok koji prelazi u narednu kalendarsku godinu konsultuje tabelu za novu godinu`() {
        // 2025-12-20 + 15 dana = 2026-01-04, nedelja -> pomera se na 2026-01-05.
        // Pozivalac ima potvrdjene podatke samo za 2025 -> 2026 treba da bude oznacena kao missing.
        val result = calculator.calculateDueDate(
            triggerDate = LocalDate.of(2025, 12, 20),
            brojDana = 15,
            yearsWithHolidayData = setOf(2025)
        )

        assertEquals(LocalDate.of(2026, 1, 4), result.rawDueDate)
        assertEquals(LocalDate.of(2026, 1, 5), result.finalDueDate)
        assertEquals(setOf(2026), result.holidayDataMissingForYears)
    }

    @Test
    fun `fallback bez podataka o praznicima i dalje primenjuje vikend korekciju`() {
        // 2026-03-07 je subota; bez ijednog podatka o praznicima, vikend korekcija i dalje radi.
        val result = calculator.calculateDueDate(
            triggerDate = LocalDate.of(2026, 2, 27),
            brojDana = 8,
            nonWorkingDays = emptySet(),
            yearsWithHolidayData = emptySet()
        )

        assertEquals(LocalDate.of(2026, 3, 9), result.finalDueDate)
        assertTrue(result.wasAdjusted)
        assertEquals(setOf(2026), result.holidayDataMissingForYears)
    }

    @Test
    fun `broj dana mora biti pozitivan`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculateDueDate(LocalDate.of(2026, 2, 15), brojDana = 0)
        }
    }
}
