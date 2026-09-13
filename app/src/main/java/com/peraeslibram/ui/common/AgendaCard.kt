package com.peraeslibram.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Red agende — bez kartice/ispune: vremenska kolona (desno ravnata, ivica desno), sadržaj,
 * povučeno vlasoglasnicom ispod ([HorizontalDivider]). "Classical" konvencija: boja kao potez
 * (ivica), ne kao ispuna — deli je ekran Rokovi i (kasnije) detalji predmeta.
 */
@Composable
fun AgendaRow(
    onClick: () -> Unit,
    time: String,
    title: String,
    modifier: Modifier = Modifier,
    timeCaption: String? = null,
    isNext: Boolean = false,
    subtitle: String? = null,
    caption: String? = null,
    caseBroj: String? = null,
    reminderIcon: ImageVector? = null,
    reminderLabel: String? = null,
    urgentLabel: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp, horizontal = 16.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.width(72.dp).fillMaxHeight()) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum"),
                        textAlign = TextAlign.End
                    )
                    timeCaption?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(
                            if (isNext) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        )
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                subtitle?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                caption?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (caseBroj != null || reminderLabel != null || urgentLabel != null) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        caseBroj?.let { OutlineTag(text = it) }
                        urgentLabel?.let {
                            TagChip(
                                text = it,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (reminderLabel != null) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                reminderIcon?.let {
                                    Icon(
                                        it,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.width(13.dp)
                                    )
                                }
                                Text(reminderLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
