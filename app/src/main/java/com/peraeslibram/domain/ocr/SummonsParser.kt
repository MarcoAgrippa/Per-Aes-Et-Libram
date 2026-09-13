package com.peraeslibram.domain.ocr

import java.time.LocalDate
import java.time.LocalTime

/**
 * Izvlači podatke o ročištu iz OCR teksta skeniranog sudskog poziva.
 *
 * Namerno odvojen od Android/ML Kit slojeva da bi ostao testabilan čistim jedinstvenim
 * testovima. Regex obrasci ovde su prvi kandidat za podešavanje kada se pojave realni
 * primerci poziva različitih sudova — formati (redosled polja, formulacije) variraju od
 * suda do suda, a ovo pokriva samo najčešće varijante na latinici i ćirilici.
 *
 * Rezultat je uvek samo predlog: pozivalac MORA prikazati ekran za potvrdu pre čuvanja,
 * nikad ne sme automatski da zakaže ročište bez pregleda korisnika.
 */
object SummonsParser {

    private val MONTHS: Map<String, Int> = mapOf(
        // latinica, genitiv
        "januara" to 1, "februara" to 2, "marta" to 3, "aprila" to 4, "maja" to 5, "juna" to 6,
        "jula" to 7, "avgusta" to 8, "septembra" to 9, "oktobra" to 10, "novembra" to 11, "decembra" to 12,
        // ćirilica, genitiv
        "јануара" to 1, "фебруара" to 2, "марта" to 3, "априла" to 4, "маја" to 5, "јуна" to 6,
        "јула" to 7, "августа" to 8, "септембра" to 9, "октобра" to 10, "новембра" to 11, "децембра" to 12
    )

    private val HEARING_KEYWORDS = listOf(
        "ročište", "rociste", "рочиште", "zakazano", "заказано",
        "poziva se", "позива се", "saslušanje", "саслушање", "pretres", "претрес"
    )

    private val TIP_ROCISTA_PHRASES = listOf(
        "glavni pretres", "главни претрес",
        "pripremno ročište", "припремно рочиште",
        "prvo ročište", "прво рочиште",
        "ročište za glavnu raspravu", "рочиште за главну расправу",
        "saslušanje stranaka", "саслушање странака",
        "usmena rasprava", "усмена расправа",
        "javna rasprava", "јавна расправа"
    )

    private val NUMERIC_DATE = Regex("""\b(\d{1,2})\.\s?(\d{1,2})\.\s?(\d{2,4})\.?""")
    private val TEXT_DATE = Regex(
        """\b(\d{1,2})\.?\s+(${MONTHS.keys.joinToString("|")})\s+(\d{4})""",
        RegexOption.IGNORE_CASE
    )
    private val TIME = Regex(
        """[uу]\s*(\d{1,2})[:,.](\d{2})\s*(?:časova|casova|часова|sati|сати|h)?""",
        RegexOption.IGNORE_CASE
    )
    // [ \t] namerno umesto \s — ime suda ne sme da "pređe" u sledeći red kad je poziv
    // formatiran kao više kratkih linija (npr. naziv suda pa odmah broj predmeta u sledećem redu).
    private val COURT = Regex(
        """(?:Osnovni|Viši|Visi|Privredni|Apelacioni|Prekršajni|Prekrsajni|Vrhovni|Upravni|""" +
            """Основни|Виши|Привредни|Апелациони|Прекршајни|Врховни|Управни)[ \t]+""" +
            """(?:sud|суд)[ \t]+(?:u|у)[ \t]+\p{Lu}\p{L}+(?:[ \t]+\p{Lu}\p{L}+)?""",
        RegexOption.IGNORE_CASE
    )
    // "soba"/"соба" dodato jer dosta poziva navodi broj sobe umesto sudnice/sale
    // (npr. "soba broj 31/II"); zato i capture grupa dozvoljava "/rimski broj" nastavak.
    private val SUDNICA = Regex(
        """(?:sudnic\p{L}*|sob\p{L}*|sal\p{L}*|судниц\p{L}*|соб\p{L}*|сал\p{L}*)\D{0,15}?(\d{1,4}(?:/\p{L}+)?)""",
        RegexOption.IGNORE_CASE
    )
    // Separator između oznake veća/postupka i broja dozvoljava i "-" (npr. "П-380/2013"),
    // ne samo tačku/razmak (npr. "P 123/26") — oba formata su uobičajena u praksi.
    private val CASE_NUMBER = Regex(
        """(?:^|[\s,;])(\p{Lu}\p{L}{0,3}[.\-\s]?\d{1,6}/\d{2,4})(?=[\s,;.]|$)"""
    )

    fun parse(text: String): ParsedSummons {
        if (text.isBlank()) return ParsedSummons(rawText = text)

        val dateMatch = closestToKeywords(TEXT_DATE.findAll(text).toList(), text)
            ?: closestToKeywords(NUMERIC_DATE.findAll(text).toList(), text)
        val timeMatch = closestToKeywords(TIME.findAll(text).toList(), text)

        return ParsedSummons(
            datum = dateMatch?.let { toDate(it) },
            vreme = timeMatch?.let { toTime(it) },
            sud = COURT.find(text)?.value?.trim(),
            sudnica = SUDNICA.find(text)?.groupValues?.get(1)?.trim(),
            tipRocista = findTipRocista(text),
            brojPredmeta = CASE_NUMBER.find(text)?.groupValues?.get(1)?.trim(),
            rawText = text
        )
    }

    private fun findTipRocista(text: String): String? {
        val lower = text.lowercase()
        val phrase = TIP_ROCISTA_PHRASES.firstOrNull { lower.contains(it.lowercase()) } ?: return null
        val start = lower.indexOf(phrase.lowercase())
        return text.substring(start, start + phrase.length)
    }

    /**
     * Bira kandidata koji se javlja najbliže POSLE neke ključne reči ročišta (npr.
     * "zakazano za 15.10.2026") — datumi/vremena tipično prate reč koja ih najavljuje,
     * pa gledamo samo unapred da datum izdavanja akta (koji često prethodi tekstu o
     * ročištu) ne bi slučajno ispao "bliži" po čistom rastojanju u karakterima.
     */
    private fun closestToKeywords(matches: List<MatchResult>, text: String): MatchResult? {
        if (matches.isEmpty()) return null
        val keywordPositions = HEARING_KEYWORDS.flatMap { kw ->
            Regex(Regex.escape(kw), RegexOption.IGNORE_CASE).findAll(text).map { it.range.first }
        }
        if (keywordPositions.isEmpty()) return matches.first()
        val afterKeyword = matches.mapNotNull { m ->
            val nearestBefore = keywordPositions.filter { it < m.range.first }.maxOrNull() ?: return@mapNotNull null
            m to (m.range.first - nearestBefore)
        }
        return afterKeyword.minByOrNull { it.second }?.first ?: matches.first()
    }

    private fun toDate(match: MatchResult): LocalDate? {
        val groups = match.groupValues
        return try {
            if (groups[2].toIntOrNull() != null) {
                // numerička: dan.mesec.godina
                val day = groups[1].toInt()
                val month = groups[2].toInt()
                var year = groups[3].toInt()
                if (year < 100) year += 2000
                LocalDate.of(year, month, day)
            } else {
                // tekstualna: dan. mesec_reč godina
                val day = groups[1].toInt()
                val month = MONTHS[groups[2].lowercase()] ?: return null
                val year = groups[3].toInt()
                LocalDate.of(year, month, day)
            }
        } catch (e: java.time.DateTimeException) {
            null
        }
    }

    private fun toTime(match: MatchResult): LocalTime? = try {
        LocalTime.of(match.groupValues[1].toInt(), match.groupValues[2].toInt())
    } catch (e: java.time.DateTimeException) {
        null
    }
}
