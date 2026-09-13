package com.peraeslibram.ui.deadlines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Warning
import com.peraeslibram.ui.common.AccentButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.domain.model.TipRadnje
import com.peraeslibram.ui.common.DatePickerField
import com.peraeslibram.ui.common.SectionHeader
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlineFormScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: DeadlineFormViewModel = hiltViewModel()
) {
    val rules by viewModel.rules.collectAsState()

    LaunchedEffect(rules, viewModel.initialTipRadnje) {
        if (viewModel.selectedRule == null && viewModel.initialTipRadnje != null) {
            rules.find { it.tipRadnje == viewModel.initialTipRadnje }?.let { viewModel.onRuleSelected(it) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procesni rok") },
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
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = viewModel.selectedRule?.nazivPrikaz ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vrsta radnje *") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    rules.forEach { rule ->
                        DropdownMenuItem(
                            text = { Text(rule.nazivPrikaz) },
                            onClick = {
                                viewModel.onRuleSelected(rule)
                                expanded = false
                            }
                        )
                    }
                }
            }
            viewModel.selectedRule?.napomenaPravno?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (viewModel.selectedRule?.tipRadnje == TipRadnje.CUSTOM_GENERICKI) {
                OutlinedTextField(
                    value = viewModel.customOpis,
                    onValueChange = { viewModel.customOpis = it },
                    label = { Text("Opis radnje") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            DatePickerField(
                label = "Datum okidača (npr. dostava presude) *",
                value = viewModel.datumOkidaca,
                onValueChange = { viewModel.onDatumOkidacaChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )

            val brojDanaError = viewModel.brojDanaError()
            OutlinedTextField(
                value = viewModel.customBrojDana,
                onValueChange = { viewModel.onCustomBrojDanaChanged(it) },
                label = { Text("Broj dana (možete promeniti, npr. produženje roka)") },
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
                isError = brojDanaError != null,
                supportingText = brojDanaError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            viewModel.calculationResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                Icons.Default.HourglassBottom,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Krajnji datum: ${result.finalDueDate.format(dateFormatter)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        if (result.wasAdjusted) {
                            Text(
                                "Napomena: rok bi po računu isticao ${result.rawDueDate.format(dateFormatter)}, ali je pomeren jer taj datum pada na neradni dan.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        if (result.holidayDataMissingForYears.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    "Upozorenje: nema potvrđenih podataka o praznicima za godinu(e) ${result.holidayDataMissingForYears.joinToString()}. " +
                                        "Vikend je uračunat, ali proverite praznike u Podešavanjima.",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
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

            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(title = "Podsetnici", icon = Icons.Default.NotificationsActive)
            DEADLINE_REMINDER_OPTIONS.forEach { (minutes, label) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = minutes in viewModel.reminderOffsets,
                        onCheckedChange = { viewModel.toggleReminder(minutes) }
                    )
                    Text(label)
                }
            }

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
