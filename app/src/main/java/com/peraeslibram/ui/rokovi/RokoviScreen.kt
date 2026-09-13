package com.peraeslibram.ui.rokovi

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.domain.model.AgendaItem
import com.peraeslibram.ui.common.AgendaEntryCard
import com.peraeslibram.ui.common.EmptyState
import com.peraeslibram.ui.common.TimeframeHeader
import com.peraeslibram.ui.common.groupByTimeframe
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RokoviScreen(
    onOpenDrawer: () -> Unit,
    onAddCase: () -> Unit,
    onOpenCase: (Long) -> Unit,
    viewModel: RokoviViewModel = hiltViewModel()
) {
    val agenda by viewModel.visibleAgenda.collectAsState()
    val filter by viewModel.filter.collectAsState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
        LaunchedEffect(Unit) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rokovi") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Meni")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddCase,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Novi predmet") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RokoviFilterChip("Sve", filter == RokoviFilter.SVE) { viewModel.setFilter(RokoviFilter.SVE) }
                RokoviFilterChip("Ročišta", filter == RokoviFilter.ROCISTA) { viewModel.setFilter(RokoviFilter.ROCISTA) }
                RokoviFilterChip("Rokovi", filter == RokoviFilter.ROKOVI) { viewModel.setFilter(RokoviFilter.ROKOVI) }
            }

            if (agenda.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.Balance,
                        title = "Nema nadolazećih obaveza",
                        subtitle = "Ročišta i rokovi koje dodate na predmetima pojaviće se ovde."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupByTimeframe(agenda) { it.dateTime.toLocalDate() }.forEach { (label, items) ->
                        item { TimeframeHeader(label) }
                        items(items, key = { "${it::class.simpleName}-${it.caseId}-${it.dateTime}" }) { agendaItem ->
                            AgendaItemRow(agendaItem, onClick = { onOpenCase(agendaItem.caseId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RokoviFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
private fun AgendaItemRow(item: AgendaItem, onClick: () -> Unit) {
    val today = LocalDate.now()
    when (item) {
        is AgendaItem.HearingItem -> {
            val isUrgent = !item.hearing.datumVreme.toLocalDate().isAfter(today)
            AgendaEntryCard(
                accentColor = if (isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                onClick = onClick,
                time = item.hearing.datumVreme.format(timeFormatter),
                timeCaption = item.hearing.sudnica?.let { "sudnica $it" },
                title = item.hearing.tipRocista ?: "Ročište",
                subtitle = item.caseNaziv,
                caption = item.hearing.sud,
                caseBroj = item.caseBroj
            )
        }
        is AgendaItem.DeadlineItem -> {
            val dueDate = item.deadline.izracunatiKrajnjiDatum
            val isLastDay = !dueDate.isAfter(today)
            AgendaEntryCard(
                accentColor = if (isLastDay) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                onClick = onClick,
                time = "Rok",
                title = item.deadline.nazivRadnjePrikaz,
                subtitle = item.caseNaziv,
                caption = "Krajnji datum: ${dueDate.format(dateFormatter)}",
                caseBroj = item.caseBroj,
                statusLabel = when {
                    item.deadline.isIstekao() -> "ISTEKAO"
                    isLastDay -> "POSLEDNJI DAN"
                    else -> null
                }
            )
        }
    }
}
