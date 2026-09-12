package com.peraeslibram.ui.hearings

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
            OutlinedTextField(
                value = viewModel.sud,
                onValueChange = { viewModel.sud = it },
                label = { Text("Sud") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.sudnica,
                onValueChange = { viewModel.sudnica = it },
                label = { Text("Sudnica") },
                leadingIcon = { Icon(Icons.Default.MeetingRoom, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.tipRocista,
                onValueChange = { viewModel.tipRocista = it },
                label = { Text("Vrsta ročišta (npr. glavni pretres)") },
                leadingIcon = { Icon(Icons.Default.Gavel, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.napomena,
                onValueChange = { viewModel.napomena = it },
                label = { Text("Napomena") },
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

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

            Button(
                onClick = { viewModel.save(onSaved) },
                enabled = viewModel.canSave(),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Sačuvaj")
            }
        }
    }
}
