package com.peraeslibram.ui.cases

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.peraeslibram.domain.model.HearingStatus
import com.peraeslibram.domain.model.Prilog
import com.peraeslibram.ui.common.IconBadge
import com.peraeslibram.ui.common.SectionHeader
import com.peraeslibram.ui.common.StatusChip
import com.peraeslibram.ui.common.TagChip
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailScreen(
    onBack: () -> Unit,
    onEditCase: (Long) -> Unit,
    onAddHearing: (Long) -> Unit,
    onEditHearing: (Long, Long) -> Unit,
    onAddDeadline: (Long) -> Unit,
    onEditDeadline: (Long, Long) -> Unit,
    onOpenAttachment: (Long, Long) -> Unit,
    onScanSummons: (Long) -> Unit,
    viewModel: CaseDetailViewModel = hiltViewModel()
) {
    val case by viewModel.case.collectAsState()
    val hearings by viewModel.hearings.collectAsState()
    val deadlines by viewModel.deadlines.collectAsState()
    val attachments by viewModel.attachments.collectAsState()

    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val scannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            val pageUris = scanResult?.pages?.map { it.imageUri }.orEmpty()
            if (pageUris.isNotEmpty()) viewModel.addAttachments(pageUris)
        }
    }

    // Poziv je jedna strana — ograničavamo na jednu stranicu i posle skeniranja idemo
    // pravo na OCR + prefill ročišta, umesto dodavanja u listu priloga kao obična stranica.
    val summonsScannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            val pageUri = scanResult?.pages?.firstOrNull()?.imageUri
            if (pageUri != null) {
                viewModel.scanSummons(pageUri) { onScanSummons(viewModel.caseId) }
            }
        }
    }

    fun launchScanner() {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
        GmsDocumentScanning.getClient(options)
            .getStartScanIntent(activity)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener {
                scope.launch { snackbarHostState.showSnackbar("Skener nije dostupan na ovom uređaju") }
            }
    }

    fun launchSummonsScanner() {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .setPageLimit(1)
            .build()
        GmsDocumentScanning.getClient(options)
            .getStartScanIntent(activity)
            .addOnSuccessListener { intentSender ->
                summonsScannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener {
                scope.launch { snackbarHostState.showSnackbar("Skener nije dostupan na ovom uređaju") }
            }
    }

    var pendingScanAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingScanAction?.invoke()
        } else {
            scope.launch { snackbarHostState.showSnackbar("Dozvola za kameru je odbijena") }
        }
        pendingScanAction = null
    }

    fun requestScanOrLaunch(action: () -> Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            action()
        } else {
            pendingScanAction = action
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(case?.naziv ?: "Predmet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                },
                actions = {
                    case?.let {
                        IconButton(onClick = { onEditCase(it.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Izmeni predmet")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            case?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        it.brojPredmeta?.let { broj ->
                            TagChip(
                                text = broj,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        InfoRow(icon = Icons.Default.Person, text = "Klijent: ${it.klijentIme}")
                        it.sud?.let { sud -> InfoRow(icon = Icons.Default.AccountBalance, text = "Sud: $sud") }
                        InfoRow(icon = Icons.Default.Business, text = "Vrsta postupka: ${it.tipPostupka}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Ročišta", icon = Icons.Default.Gavel)
            Spacer(modifier = Modifier.height(8.dp))
            hearings.forEach { hearing ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        .clickable { onEditHearing(viewModel.caseId, hearing.id) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconBadge(
                            icon = Icons.Default.Gavel,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            size = 36.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(hearing.datumVreme.format(dateTimeFormatter), style = MaterialTheme.typography.titleSmall)
                                if (hearing.status == HearingStatus.ZAKAZANO) {
                                    TagChip(
                                        text = "ZAKAZANO",
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            hearing.sud?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = { viewModel.deleteHearing(hearing) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Obriši ročište", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = { onAddHearing(viewModel.caseId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Dodaj ročište")
            }
            OutlinedButton(
                onClick = { requestScanOrLaunch(::launchSummonsScanner) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Skeniraj poziv")
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Rokovi", icon = Icons.Default.HourglassBottom)
            Spacer(modifier = Modifier.height(8.dp))
            deadlines.forEach { deadline ->
                val isIstekao = deadline.isIstekao()
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        .clickable { onEditDeadline(viewModel.caseId, deadline.id) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconBadge(
                            icon = if (isIstekao) Icons.Default.EventBusy else Icons.Default.HourglassBottom,
                            containerColor = if (isIstekao) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = if (isIstekao) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                            size = 36.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(deadline.nazivRadnjePrikaz, style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Krajnji datum: ${deadline.izracunatiKrajnjiDatum.format(dateFormatter)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            when {
                                isIstekao -> StatusChip(
                                    text = "ISTEKAO",
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                deadline.izracunatiKrajnjiDatum == LocalDate.now() -> StatusChip(
                                    text = "POSLEDNJI DAN",
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.deleteDeadline(deadline) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Obriši rok", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = { onAddDeadline(viewModel.caseId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Dodaj rok")
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Prilozi", icon = Icons.Default.AttachFile)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(attachments, key = { it.id }) { prilog ->
                    AttachmentThumbnail(
                        prilog = prilog,
                        file = viewModel.resolveAttachmentFile(prilog),
                        onClick = { onOpenAttachment(viewModel.caseId, prilog.id) },
                        onDelete = { viewModel.deleteAttachment(prilog) },
                        onRename = { newName -> viewModel.renameAttachment(prilog, newName) }
                    )
                }
                item {
                    AddAttachmentTile(onClick = { requestScanOrLaunch(::launchScanner) })
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.height(18.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun AttachmentThumbnail(
    prilog: Prilog,
    file: java.io.File,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    var showRenameDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.size(96.dp)) {
        Card(
            modifier = Modifier.fillMaxSize().clickable(onClick = onClick),
            shape = RoundedCornerShape(14.dp)
        ) {
            AsyncImage(
                model = file,
                contentDescription = prilog.naziv,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.align(Alignment.TopEnd).size(28.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Obriši prilog",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(
            onClick = { showRenameDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).size(28.dp)
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Preimenuj prilog",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    if (showRenameDialog) {
        var newName by remember { mutableStateOf(prilog.naziv) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Preimenuj prilog") },
            text = {
                OutlinedTextField(value = newName, onValueChange = { newName = it }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    onRename(newName)
                    showRenameDialog = false
                }) { Text("Sačuvaj") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Otkaži") }
            }
        )
    }
}

@Composable
private fun AddAttachmentTile(onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(96.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.DocumentScanner,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                "Dodaj",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
