package com.peraeslibram.ui.notaries

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.domain.model.Notary
import com.peraeslibram.ui.common.EmptyState
import com.peraeslibram.ui.common.OutlinedFab
import com.peraeslibram.ui.common.initialsOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaryListScreen(
    onOpenDrawer: () -> Unit,
    onAddNotary: () -> Unit,
    onEditNotary: (Long) -> Unit,
    viewModel: NotaryListViewModel = hiltViewModel()
) {
    val notaries by viewModel.notaries.collectAsState()
    val query by viewModel.query.collectAsState()
    var pendingDelete by remember { mutableStateOf<Notary?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Javni beležnici") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Meni")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Pretraži javne beležnike") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Obriši pretragu")
                            }
                        }
                    },
                    singleLine = true
                )

                if (notaries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(
                            icon = Icons.Default.Description,
                            title = if (query.isBlank()) "Još nema javnih beležnika" else "Nema rezultata",
                            subtitle = if (query.isBlank()) {
                                "Dodajte prvog javnog beležnika dugmetom + u donjem uglu."
                            } else {
                                "Nijedan javni beležnik ne odgovara pretrazi „$query“."
                            }
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(notaries, key = { it.id }) { notary ->
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onEditNotary(notary.id) }
                                        .padding(horizontal = 20.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        initialsOf(notary.naziv),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(notary.naziv, style = MaterialTheme.typography.titleMedium)
                                        notary.adresa?.let {
                                            Text(
                                                it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        notary.telefon?.let {
                                            Text(
                                                it,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        notary.email?.let {
                                            Text(
                                                it,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    if (!notary.adresa.isNullOrBlank()) {
                                        IconButton(onClick = {
                                            val mapQuery = Uri.encode("${notary.naziv}, ${notary.adresa}")
                                            val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$mapQuery")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                        }) {
                                            Icon(
                                                Icons.Default.Map,
                                                contentDescription = "Otvori adresu na mapi",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    IconButton(onClick = { pendingDelete = notary }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Obriši javnog beležnika",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }

            OutlinedFab(
                onClick = onAddNotary,
                icon = Icons.Default.Add,
                label = "Novi beležnik",
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
            )
        }
    }

    pendingDelete?.let { notary ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Obriši javnog beležnika?") },
            text = { Text("Javni beležnik „${notary.naziv}\" će biti trajno obrisan.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(notary)
                    pendingDelete = null
                }) { Text("Obriši") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Otkaži") }
            }
        )
    }
}
