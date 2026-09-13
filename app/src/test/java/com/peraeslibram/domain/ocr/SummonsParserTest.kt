package com.peraeslibram.domain.ocr

import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Test

class SummonsParserTest {

    @Test
    fun `prazan tekst daje prazan rezultat`() {
        val result = SummonsParser.parse("")
        assertThat(result.datum).isNull()
        assertThat(result.vreme).isNull()
    }

    @Test
    fun `latinica - numericki datum i vreme uz kljucnu rec rocista`() {
        val text = """
            OSNOVNI SUD U BEOGRADU
            Broj predmeta: P 123/26

            Ovim putem se poziva na ročište zakazano za dan 15.10.2026. godine u 10:00 časova,
            sudnica 5, radi glavnog pretresa.
        """.trimIndent()

        val result = SummonsParser.parse(text)

        assertThat(result.datum).isEqualTo(LocalDate.of(2026, 10, 15))
        assertThat(result.vreme).isEqualTo(LocalTime.of(10, 0))
        assertThat(result.sud).isEqualTo("OSNOVNI SUD U BEOGRADU")
        assertThat(result.sudnica).isEqualTo("5")
        assertThat(result.brojPredmeta).isEqualTo("P 123/26")
    }

    @Test
    fun `latinica - tekstualni datum`() {
        val text = "Ročište je zakazano za 3. novembra 2026. godine u 9,30 sati."

        val result = SummonsParser.parse(text)

        assertThat(result.datum).isEqualTo(LocalDate.of(2026, 11, 3))
        assertThat(result.vreme).isEqualTo(LocalTime.of(9, 30))
    }

    @Test
    fun `cirilica - sud i broj predmeta`() {
        val text = """
            ОСНОВНИ СУД У НИШУ
            Позива се на рочиште дана 20.10.2026. у 11:00 часова, судница 3.
            Број предмета: К 45/26
        """.trimIndent()

        val result = SummonsParser.parse(text)

        assertThat(result.datum).isEqualTo(LocalDate.of(2026, 10, 20))
        assertThat(result.vreme).isEqualTo(LocalTime.of(11, 0))
        assertThat(result.sud).isEqualTo("ОСНОВНИ СУД У НИШУ")
        assertThat(result.sudnica).isEqualTo("3")
        assertThat(result.brojPredmeta).isEqualTo("К 45/26")
    }

    @Test
    fun `bira datum najblizi kljucnoj reci rocista, ne datum izdavanja akta`() {
        val text = """
            Beograd, 01.09.2026.
            Poziva se na ročište zakazano za 15.10.2026. u 12:00 časova.
        """.trimIndent()

        val result = SummonsParser.parse(text)

        assertThat(result.datum).isEqualTo(LocalDate.of(2026, 10, 15))
    }

    @Test
    fun `tip rocista prepoznat iz poznate fraze`() {
        val text = "Zakazuje se pripremno ročište za 15.10.2026. u 10:00 časova."

        val result = SummonsParser.parse(text)

        assertThat(result.tipRocista).isEqualTo("pripremno ročište")
    }

    @Test
    fun `cirilica - stvaran poziv Viseg suda, broj predmeta sa crticom i soba umesto sudnice`() {
        val text = """
            РЕПУБЛИКА СРБИЈА
            ВИШИ СУД У БЕОГРАДУ
            Тимочка 15
            17.09.2013.год.

            6  П-380/2013

            ПОЗИВ ЗА ГЛАВНУ РАСПРАВУ

            ДРАГАН МАРИЋ из Ваљева позива се као ТУЖИЛАЦ да дође дана
            26.11.2013. године у 13,00 часова у овај суд, соба број 31/II у правној ствари
        """.trimIndent()

        val result = SummonsParser.parse(text)

        assertThat(result.datum).isEqualTo(LocalDate.of(2013, 11, 26))
        assertThat(result.vreme).isEqualTo(LocalTime.of(13, 0))
        assertThat(result.sud).isEqualTo("ВИШИ СУД У БЕОГРАДУ")
        assertThat(result.sudnica).isEqualTo("31/II")
        assertThat(result.brojPredmeta).isEqualTo("П-380/2013")
    }
}
