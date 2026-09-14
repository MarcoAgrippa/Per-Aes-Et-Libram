package com.peraeslibram.ui.cases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import com.peraeslibram.ui.common.AccentButton
import com.peraeslibram.ui.common.Text
import com.peraeslibram.ui.common.displayText
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.domain.model.TipPostupka
import com.peraeslibram.domain.model.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseFormScreen(
    onSaved: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: CaseFormViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Predmet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = viewModel.naziv,
                onValueChange = { viewModel.naziv = it },
                label = { Text("Naziv predmeta *") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.brojPredmeta,
                onValueChange = { viewModel.brojPredmeta = it },
                label = { Text("Broj predmeta") },
                leadingIcon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.klijentIme,
                onValueChange = { viewModel.klijentIme = it },
                label = { Text("Klijent *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.klijentKontakt,
                onValueChange = { viewModel.klijentKontakt = it },
                label = { Text("Kontakt klijenta") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            val courtSuggestions by viewModel.courtSuggestions.collectAsState()
            var sudMenuExpanded by remember { mutableStateOf(false) }
            val sudExpanded = sudMenuExpanded && courtSuggestions.isNotEmpty()
            ExposedDropdownMenuBox(
                expanded = sudExpanded,
                onExpandedChange = { sudMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = displayText(viewModel.sud),
                    onValueChange = {
                        viewModel.onSudChange(it)
                        sudMenuExpanded = true
                    },
                    label = { Text("Sud") },
                    leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                    trailingIcon = {
                        if (courtSuggestions.isNotEmpty()) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = sudExpanded)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = sudExpanded,
                    onDismissRequest = { sudMenuExpanded = false }
                ) {
                    courtSuggestions.forEach { court ->
                        DropdownMenuItem(
                            text = { Text(court.naziv) },
                            onClick = {
                                viewModel.selectCourt(court)
                                sudMenuExpanded = false
                            }
                        )
                    }
                }
            }

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = displayText(viewModel.tipPostupka.label),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vrsta postupka") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    TipPostupka.entries.forEach { tip ->
                        DropdownMenuItem(
                            text = { Text(tip.label) },
                            onClick = {
                                viewModel.tipPostupka = tip
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = viewModel.napomena,
                onValueChange = { viewModel.napomena = it },
                label = { Text("Napomena") },
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            AccentButton(
                onClick = { viewModel.save(onSaved) },
                enabled = viewModel.canSave(),
                icon = Icons.Default.Check,
                text = "Sačuvaj",
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }
    }
}
