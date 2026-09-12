package com.peraeslibram.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/**
 * Grupiše stavke u vremenske korpe koje dele agenda na početnom ekranu i ekran "Rokovi".
 * Jedan izvor istine za nazive i granice korpi — inače bi dva ekrana neprimetno razišla.
 */
fun <T> groupByTimeframe(
    items: List<T>,
    today: LocalDate = LocalDate.now(),
    dateOf: (T) -> LocalDate
): List<Pair<String, List<T>>> {
    val tomorrow = today.plusDays(1)
    val endOfWeek = today.plusDays(7)

    val prosli = items.filter { dateOf(it).isBefore(today) }
    val danas = items.filter { dateOf(it) == today }
    val sutra = items.filter { dateOf(it) == tomorrow }
    val oveNedelje = items.filter {
        val d = dateOf(it)
        d.isAfter(tomorrow) && !d.isAfter(endOfWeek)
    }
    val kasnije = items.filter { dateOf(it).isAfter(endOfWeek) }

    return listOf(
        "Prošli / istekli" to prosli,
        "Danas" to danas,
        "Sutra" to sutra,
        "Ove nedelje" to oveNedelje,
        "Kasnije" to kasnije
    ).filter { it.second.isNotEmpty() }
}

@Composable
fun TimeframeHeader(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}
