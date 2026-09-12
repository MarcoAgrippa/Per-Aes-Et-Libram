package com.peraeslibram.ui.courts

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.domain.model.Court
import com.peraeslibram.ui.common.EmptyState
import com.peraeslibram.ui.common.IconBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtListScreen(
    onOpenDrawer: () -> Unit,
    onAddCourt: () -> Unit,
    onEditCourt: (Long) -> Unit,
    viewModel: CourtListViewModel = hiltViewModel()
) {
    val courts by viewModel.courts.collectAsState()
    var pendingDelete by remember { mutableStateOf<Court?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sudovi") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Meni")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCourt,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novi sud")
            }
        }
    ) { padding ->
        if (courts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Default.AccountBalance,
                    title = "Još nema sudova",
                    subtitle = "Dodajte prvi sud dugmetom + u donjem uglu."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(courts, key = { it.id }) { court ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onEditCourt(court.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconBadge(
                                icon = Icons.Default.AccountBalance,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(court.naziv, style = MaterialTheme.typography.titleMedium)
                                court.adresa?.let {
                                    Text(it, style = MaterialTheme.typography.bodyMedium)
                                }
                                court.telefon?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                court.email?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { pendingDelete = court }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Obriši sud",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { court ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Obriši sud?") },
            text = { Text("Sud „${court.naziv}\" će biti trajno obrisan.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(court)
                    pendingDelete = null
                }) { Text("Obriši") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Otkaži") }
            }
        )
    }
}
