package com.peraeslibram.data.local.seed

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Golden-value testovi naspram javno potvrđenih datuma pravoslavnog Vaskrsa:
 * 2024-05-05, 2025-04-20, 2026-04-12, 2027-05-02, 2028-04-16.
 */
class OrthodoxEasterCalculatorTest {

    @Test
    fun `orthodox easter 2024`() {
        assertEquals(LocalDate.of(2024, 5, 5), OrthodoxEasterCalculator.orthodoxEasterSunday(2024))
    }

    @Test
    fun `orthodox easter 2025`() {
        assertEquals(LocalDate.of(2025, 4, 20), OrthodoxEasterCalculator.orthodoxEasterSunday(2025))
    }

    @Test
    fun `orthodox easter 2026`() {
        assertEquals(LocalDate.of(2026, 4, 12), OrthodoxEasterCalculator.orthodoxEasterSunday(2026))
    }

    @Test
    fun `orthodox easter 2027`() {
        assertEquals(LocalDate.of(2027, 5, 2), OrthodoxEasterCalculator.orthodoxEasterSunday(2027))
    }

    @Test
    fun `orthodox easter 2028`() {
        assertEquals(LocalDate.of(2028, 4, 16), OrthodoxEasterCalculator.orthodoxEasterSunday(2028))
    }

    @Test
    fun `great friday is two days before easter sunday`() {
        val easter = OrthodoxEasterCalculator.orthodoxEasterSunday(2026)
        assertEquals(easter.minusDays(2), OrthodoxEasterCalculator.greatFriday(2026))
    }

    @Test
    fun `easter monday is one day after easter sunday`() {
        val easter = OrthodoxEasterCalculator.orthodoxEasterSunday(2026)
        assertEquals(easter.plusDays(1), OrthodoxEasterCalculator.easterMonday(2026))
    }
}
