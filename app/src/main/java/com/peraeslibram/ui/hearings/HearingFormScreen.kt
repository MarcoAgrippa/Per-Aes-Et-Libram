package com.peraeslibram.ui.hearings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.ui.common.DatePickerField
import com.peraeslibram.ui.common.SectionHeader
import com.peraeslibram.ui.common.TimePickerField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HearingFormScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: HearingFormViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ročište") },
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
            viewModel.caseNumberMismatchWarning?.let { warning ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        Text(warning, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
            DatePickerField(
                label = "Datum ročišta *",
                value = viewModel.datum,
                onValueChange = { viewModel.datum = it },
                modifier = Modifier.fillMaxWidth()
            )
            TimePickerField(
                label = "Vreme ročišta *",
                value = viewModel.vreme,
                onValueChange = { viewModel.vreme = it },
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
                    value = viewModel.sud,
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
            OutlinedTextField(
                value = viewModel.sudnica,
                onValueChange = { viewModel.sudnica = it },
                label = { Text("Sudnica") },
                leadingIcon = { Icon(Icons.Default.MeetingRoom, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            var tipMenuExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = tipMenuExpanded,
                onExpandedChange = { tipMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = viewModel.tipRocista,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vrsta ročišta") },
                    leadingIcon = { Icon(Icons.Default.Gavel, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipMenuExpanded) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = tipMenuExpanded,
                    onDismissRequest = { tipMenuExpanded = false }
                ) {
                    HEARING_TYPE_OPTIONS.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.tipRocista = option
                                tipMenuExpanded = false
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

            viewModel.ocrRawText?.let { rawText -> OcrTextSection(rawText) }

            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(title = "Podsetnici", icon = Icons.Default.NotificationsActive)
            HEARING_REMINDER_OPTIONS.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = option.minutesBefore in viewModel.reminderOffsets,
                        onCheckedChange = { viewModel.toggleReminder(option.minutesBefore) }
                    )
                    Text(option.label)
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

@Composable
private fun OcrTextSection(rawText: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Prepoznat tekst sa poziva (za proveru)", style = MaterialTheme.typography.labelLarge)
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
            if (expanded) {
                Text(
                    "Automatsko prepoznavanje teksta nije uvek tačno — polja iznad proverite pre čuvanja.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                Text(
                    rawText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}
