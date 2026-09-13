package com.peraeslibram.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Red agende sa obojenom trakom hitnosti levo (ročište/rok) — deli je ekran "Rokovi"
 * i detalji predmeta, da vremenski unosi izgledaju isto gde god se prikažu.
 */
@Composable
fun AgendaEntryCard(
    accentColor: Color,
    onClick: () -> Unit,
    time: String,
    title: String,
    modifier: Modifier = Modifier,
    timeCaption: String? = null,
    subtitle: String? = null,
    caption: String? = null,
    caseBroj: String? = null,
    statusLabel: String? = null,
    statusContainerColor: Color = MaterialTheme.colorScheme.errorContainer,
    statusContentColor: Color = MaterialTheme.colorScheme.onErrorContainer
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )
            Row(
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.width(56.dp)) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.titleMedium
                    )
                    timeCaption?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleSmall)
                    subtitle?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                    caption?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (caseBroj != null || statusLabel != null) {
                        Row(
                            modifier = Modifier.padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            caseBroj?.let {
                                TagChip(
                                    text = it,
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            statusLabel?.let {
                                TagChip(text = it, containerColor = statusContainerColor, contentColor = statusContentColor)
                            }
                        }
                    }
                }
            }
        }
    }
}
