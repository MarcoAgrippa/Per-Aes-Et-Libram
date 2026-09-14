package com.peraeslibram.ui.common

import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import org.junit.Test

class TimeGroupingTest {

    // Subota, 12. septembar 2026.
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

        // Grupisanje je po tačnom danu (ne po nedeljnim "Sutra"/"Ove nedelje" korpama) —
        // vidi dokumentaciju uz groupByTimeframe u TimeGrouping.kt.
        assertThat(grupe.map { it.first }).containsExactly(
            "Prošli / istekli",
            "Danas",
            "Nedelja, 13. septembar",
            "Sreda, 16. septembar",
            "Ponedeljak, 12. oktobar"
        ).inOrder()
        assertThat(grupe.map { it.second.single() }).isEqualTo(stavke)
    }

    @Test
    fun `svaki buduci dan dobija sopstvenu grupu bez obzira koliko je daleko`() {
        val grupe = groupByTimeframe(
            listOf(danas.plusDays(7), danas.plusDays(8)),
            today = danas
        ) { it }

        assertThat(grupe.map { it.first }).containsExactly(
            "Subota, 19. septembar",
            "Nedelja, 20. septembar"
        ).inOrder()
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
