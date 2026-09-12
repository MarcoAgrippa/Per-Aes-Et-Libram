package com.peraeslibram.ui.common

import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import org.junit.Test

class TimeGroupingTest {

    private val danas = LocalDate.of(2026, 9, 12)

    @Test
    fun `svaka stavka ide u svoju vremensku korpu`() {
        val stavke = listOf(
            danas.minusDays(3),
            danas,
            danas.plusDays(1),
            danas.plusDays(4),
            danas.plusDays(30)
        )

        val grupe = groupByTimeframe(stavke, today = danas) { it }

        assertThat(grupe.map { it.first }).containsExactly(
            "Prošli / istekli", "Danas", "Sutra", "Ove nedelje", "Kasnije"
        ).inOrder()
        assertThat(grupe.map { it.second.single() }).isEqualTo(stavke)
    }

    @Test
    fun `sedmi dan je jos uvek ove nedelje, osmi je kasnije`() {
        val grupe = groupByTimeframe(
            listOf(danas.plusDays(7), danas.plusDays(8)),
            today = danas
        ) { it }

        assertThat(grupe.single { it.first == "Ove nedelje" }.second).containsExactly(danas.plusDays(7))
        assertThat(grupe.single { it.first == "Kasnije" }.second).containsExactly(danas.plusDays(8))
    }

    @Test
    fun `prazne korpe se izostavljaju`() {
        val grupe = groupByTimeframe(listOf(danas), today = danas) { it }

        assertThat(grupe).hasSize(1)
        assertThat(grupe.single().first).isEqualTo("Danas")
    }

    @Test
    fun `prazan ulaz daje praznu listu grupa`() {
        assertThat(groupByTimeframe(emptyList<LocalDate>(), today = danas) { it }).isEmpty()
    }
}
