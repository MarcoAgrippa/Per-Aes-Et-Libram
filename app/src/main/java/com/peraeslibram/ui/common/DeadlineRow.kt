package com.peraeslibram.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.peraeslibram.domain.model.Deadline
import java.time.format.DateTimeFormatter

private val deadlineDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

/**
 * Sadržaj reda za rok (bedž + tekst). Dele ga agenda na početnom ekranu i ekran "Rokovi",
 * da bi prikaz roka bio identičan po konstrukciji, a ne po dogovoru.
 */
@Composable
fun RowScope.DeadlineRowContent(deadline: Deadline, caseNaziv: String?) {
    val isIstekao = deadline.isIstekao()
    IconBadge(
        icon = if (isIstekao) Icons.Default.EventBusy else Icons.Default.HourglassBottom,
        containerColor = if (isIstekao) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        contentColor = if (isIstekao) {
            MaterialTheme.colorScheme.onErrorContainer
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        }
    )
    Column(modifier = Modifier.weight(1f)) {
        Text("Rok — ${caseNaziv ?: "(bez naziva)"}", style = MaterialTheme.typography.titleSmall)
        Text(deadline.nazivRadnjePrikaz, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "Krajnji datum: ${deadline.izracunatiKrajnjiDatum.format(deadlineDateFormatter)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (deadline.krajnjiDatumPomeren) {
            Text(
                "Pomeren sa ${deadline.originalniKrajnjiDatum?.format(deadlineDateFormatter)} (neradni dan)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isIstekao) {
            StatusChip(
                text = "ISTEKAO",
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/** Samostalna kartica roka — koristi je ekran "Rokovi". */
@Composable
fun DeadlineCard(
    deadline: Deadline,
    caseNaziv: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DeadlineRowContent(deadline, caseNaziv)
        }
    }
}
