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
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.peraeslibram.domain.model.label
import com.peraeslibram.ui.common.AccentButton
import com.peraeslibram.ui.common.SectionHeader
import com.peraeslibram.ui.common.TagChip
import com.peraeslibram.ui.common.Text
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    it.brojPredmeta?.let { broj ->
                        Text(
                            broj,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(it.naziv, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        listOfNotNull(it.klijentIme, it.sud, it.tipPostupka.label).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Ročišta", icon = Icons.Default.Gavel)
            Spacer(modifier = Modifier.height(4.dp))
            hearings.forEach { hearing ->
                val isPast = hearing.status != HearingStatus.ZAKAZANO
                CaseTimelineRow(
                    onClick = { onEditHearing(viewModel.caseId, hearing.id) },
                    onDelete = { viewModel.deleteHearing(hearing) },
                    deleteDescription = "Obriši ročište",
                    dimmed = isPast,
                    dateText = hearing.datumVreme.format(dateTimeFormatter),
                    title = hearing.tipRocista ?: "Ročište",
                    caption = listOfNotNull(
                        hearing.sud,
                        if (isPast) "održano" else null
                    ).joinToString(" · ").ifBlank { null }
                )
            }
            AccentButton(
                onClick = { onAddHearing(viewModel.caseId) },
                icon = Icons.Default.Add,
                text = "Dodaj ročište",
                modifier = Modifier.fillMaxWidth()
            )
            AccentButton(
                onClick = { requestScanOrLaunch(::launchSummonsScanner) },
                icon = Icons.Default.DocumentScanner,
                text = "Skeniraj poziv",
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Rokovi", icon = Icons.Default.HourglassBottom)
            Spacer(modifier = Modifier.height(4.dp))
            deadlines.forEach { deadline ->
                val isIstekao = deadline.isIstekao()
                val isLastDay = deadline.izracunatiKrajnjiDatum == LocalDate.now()
                CaseTimelineRow(
                    onClick = { onEditDeadline(viewModel.caseId, deadline.id) },
                    onDelete = { viewModel.deleteDeadline(deadline) },
                    deleteDescription = "Obriši rok",
                    dimmed = false,
                    dateText = "Krajnji datum: ${deadline.izracunatiKrajnjiDatum.format(dateFormatter)}",
                    title = deadline.nazivRadnjePrikaz,
                    urgentLabel = when {
                        isIstekao -> "istekao"
                        isLastDay -> "poslednji dan"
                        else -> null
                    }
                )
            }
            AccentButton(
                onClick = { onAddDeadline(viewModel.caseId) },
                icon = Icons.Default.Add,
                text = "Dodaj rok",
                modifier = Modifier.fillMaxWidth()
            )

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

/** Red ročišta/roka u detaljima predmeta — tačka umesto ikone-bedža, ivica ispod umesto kartice. */
@Composable
private fun CaseTimelineRow(
    onClick: () -> Unit,
    onDelete: () -> Unit,
    deleteDescription: String,
    dimmed: Boolean,
    dateText: String,
    title: String,
    caption: String? = null,
    urgentLabel: String? = null
) {
    val emphasisColor = if (dimmed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(modifier = Modifier.padding(top = 7.dp).size(7.dp)) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(7.dp)) {
                    drawCircle(color = emphasisColor)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(dateText, style = MaterialTheme.typography.bodySmall, color = emphasisColor)
                Text(title, style = if (dimmed) MaterialTheme.typography.titleMedium.copy(color = emphasisColor) else MaterialTheme.typography.titleMedium)
                caption?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                urgentLabel?.let {
                    TagChip(
                        text = it,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = deleteDescription, tint = MaterialTheme.colorScheme.error)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
