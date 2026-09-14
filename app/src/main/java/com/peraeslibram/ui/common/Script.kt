package com.peraeslibram.ui.common

enum class Script { LATIN, CYRILLIC }

private val digraphs = linkedMapOf(
    "Lj" to "Љ", "LJ" to "Љ", "lj" to "љ",
    "Nj" to "Њ", "NJ" to "Њ", "nj" to "њ",
    "Dž" to "Џ", "DŽ" to "Џ", "dž" to "џ"
)

private val singleLetters = mapOf(
    'A' to "А", 'a' to "а",
    'B' to "Б", 'b' to "б",
    'V' to "В", 'v' to "в",
    'G' to "Г", 'g' to "г",
    'D' to "Д", 'd' to "д",
    'Đ' to "Ђ", 'đ' to "ђ",
    'E' to "Е", 'e' to "е",
    'Ž' to "Ж", 'ž' to "ж",
    'Z' to "З", 'z' to "з",
    'I' to "И", 'i' to "и",
    'J' to "Ј", 'j' to "ј",
    'K' to "К", 'k' to "к",
    'L' to "Л", 'l' to "л",
    'M' to "М", 'm' to "м",
    'N' to "Н", 'n' to "н",
    'O' to "О", 'o' to "о",
    'P' to "П", 'p' to "п",
    'R' to "Р", 'r' to "р",
    'S' to "С", 's' to "с",
    'T' to "Т", 't' to "т",
    'Ć' to "Ћ", 'ć' to "ћ",
    'U' to "У", 'u' to "у",
    'F' to "Ф", 'f' to "ф",
    'H' to "Х", 'h' to "х",
    'C' to "Ц", 'c' to "ц",
    'Č' to "Ч", 'č' to "ч",
    'Š' to "Ш", 'š' to "ш"
)

/**
 * Prevodi srpski tekst pisan latinicom u ćirilicu. Digrafi (Lj/Nj/Dž) se obrađuju pre
 * pojedinačnih slova jer inače "nj" postaje "нј" umesto "њ". Slova van srpske abecede
 * (q, w, x, y, brojevi, interpunkcija...) prolaze nepromenjena.
 */
fun String.toCyrillic(): String {
    val result = StringBuilder(length)
    var i = 0
    while (i < length) {
        val twoChar = if (i + 1 < length) substring(i, i + 2) else null
        val digraph = twoChar?.let { digraphs[it] }
        if (digraph != null) {
            result.append(digraph)
            i += 2
        } else {
            result.append(singleLetters[this[i]] ?: this[i].toString())
            i += 1
        }
    }
    return result.toString()
}

/**
 * Poređenje otporno na pismo — podatak je uskladišten latinicom, ali korisnik može kucati
 * ćirilicom (tastatura mu je na ćirilici, ili je uz uključenu opciju Ćirilica prirodno da tako
 * i kuca). Umesto da postoji odvojena ćirilica→latinica tabela, upoređuje se izvorni tekst SA
 * već postojećom latinica→ćirilica transliteracijom istog teksta.
 */
fun String.matchesQuery(query: String): Boolean =
    contains(query, ignoreCase = true) || toCyrillic().contains(query, ignoreCase = true)
