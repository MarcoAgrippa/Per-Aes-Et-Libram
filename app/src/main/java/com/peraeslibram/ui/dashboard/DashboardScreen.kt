package com.peraeslibram.ui.dashboard

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.domain.model.AgendaItem
import com.peraeslibram.ui.common.EmptyState
import com.peraeslibram.ui.common.IconBadge
import com.peraeslibram.ui.common.StatusChip
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM. HH:mm")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onOpenCases: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCase: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val agenda by viewModel.agenda.collectAsState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(
                            Icons.Default.Balance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text("Per Aes Et Libram", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Podešavanja", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenCases,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Predmeti")
            }
        }
    ) { padding ->
        if (agenda.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Default.Balance,
                    title = "Nema nadolazećih obaveza",
                    subtitle = "Ročišta i rokovi koje dodate na predmetima pojaviće se ovde."
                )
            }
        } else {
            val grouped = groupAgenda(agenda)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { (label, items) ->
                    item {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                        )
                    }
                    items(items) { agendaItem ->
                        AgendaItemCard(agendaItem, onClick = { onOpenCase(agendaItem.caseId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendaItemCard(item: AgendaItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (item) {
                is AgendaItem.HearingItem -> {
                    IconBadge(
                        icon = Icons.Default.Gavel,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ročište — ${item.caseNaziv ?: "(bez naziva)"}", style = MaterialTheme.typography.titleSmall)
                        Text(item.hearing.datumVreme.format(dateTimeFormatter), style = MaterialTheme.typography.bodyMedium)
                        item.hearing.sud?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                is AgendaItem.DeadlineItem -> {
                    val isIstekao = item.deadline.isIstekao()
                    IconBadge(
                        icon = if (isIstekao) Icons.Default.EventBusy else Icons.Default.HourglassBottom,
                        containerColor = if (isIstekao) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (isIstekao) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Rok — ${item.caseNaziv ?: "(bez naziva)"}", style = MaterialTheme.typography.titleSmall)
                        Text(item.deadline.nazivRadnjePrikaz, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "Krajnji datum: ${item.deadline.izracunatiKrajnjiDatum.format(dateFormatter)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (item.deadline.krajnjiDatumPomeren) {
                            Text(
                                "Pomeren sa ${item.deadline.originalniKrajnjiDatum?.format(dateFormatter)} (neradni dan)",
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
            }
        }
    }
}

private fun groupAgenda(items: List<AgendaItem>): List<Pair<String, List<AgendaItem>>> {
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)
    val endOfWeek = today.plusDays(7)

    val danas = items.filter { it.dateTime.toLocalDate() == today }
    val sutra = items.filter { it.dateTime.toLocalDate() == tomorrow }
    val oveNedelje = items.filter {
        val d = it.dateTime.toLocalDate()
        d.isAfter(tomorrow) && !d.isAfter(endOfWeek)
    }
    val kasnije = items.filter { it.dateTime.toLocalDate().isAfter(endOfWeek) }
    val prosli = items.filter { it.dateTime.toLocalDate().isBefore(today) }

    return listOf(
        "Prošli / istekli" to prosli,
        "Danas" to danas,
        "Sutra" to sutra,
        "Ove nedelje" to oveNedelje,
        "Kasnije" to kasnije
    ).filter { it.second.isNotEmpty() }
}
