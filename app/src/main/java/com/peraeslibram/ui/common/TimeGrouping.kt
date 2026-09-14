package com.peraeslibram.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val WEEKDAYS = arrayOf("Ponedeljak", "Utorak", "Sreda", "Četvrtak", "Petak", "Subota", "Nedelja")
private val MONTHS = arrayOf(
    "januar", "februar", "mart", "april", "maj", "jun",
    "jul", "avgust", "septembar", "oktobar", "novembar", "decembar"
)
private val fullDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

/** "Sreda, 16. septembar" — dan u nedelji ispisan iz [WEEKDAYS]/[MONTHS] jer JDK-ova sr-Cyrl/Latn lokalizacija nije dostupna bez dodatnih resursa. */
private fun dayLabel(date: LocalDate): String =
    "${WEEKDAYS[date.dayOfWeek.value - 1]}, ${date.dayOfMonth}. ${MONTHS[date.monthValue - 1]}"

/**
 * Grupiše stavke po tačnom danu (ne po nedeljnim "korpama") — "Danas" posebno, ostalo kao
 * "Sreda, 16. septembar", uz zaseban katalog za prošle/istekle. Ovo je namerno usklađeno s
 * mock-om "Rokovnik - Ekrani" (01 Rokovi), koji ne koristi "Sutra"/"Ove nedelje" korpe.
 */
fun <T> groupByTimeframe(
    items: List<T>,
    today: LocalDate = LocalDate.now(),
    dateOf: (T) -> LocalDate
): List<Pair<String, List<T>>> {
    val prosli = items.filter { dateOf(it).isBefore(today) }
    val buduci = items.filter { !dateOf(it).isBefore(today) }

    val prosliGroup = if (prosli.isNotEmpty()) listOf("Prošli / istekli" to prosli) else emptyList()
    val buduciGroups = buduci
        .groupBy { dateOf(it) }
        .toSortedMap()
        .map { (date, dayItems) ->
            val label = if (date == today) "Danas" else dayLabel(date)
            label to dayItems
        }

    return prosliGroup + buduciGroups
}

/** Kicker + vlasoglasnica koja puni ostatak reda — "Danas" dobija i datum uz desnu ivicu. */
@Composable
fun TimeframeHeader(label: String, modifier: Modifier = Modifier, trailingDate: LocalDate? = null) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 14.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (label == "Danas") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(modifier = Modifier.weight(1f).padding(bottom = 5.dp)) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
        if (trailingDate != null) {
            Text(
                text = trailingDate.format(fullDateFormatter),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
        }
    }
}
