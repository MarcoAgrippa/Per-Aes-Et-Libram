package com.peraeslibram.ui.cases

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.data.local.storage.AttachmentFileStore
import com.peraeslibram.domain.model.Case
import com.peraeslibram.domain.model.Deadline
import com.peraeslibram.domain.model.Hearing
import com.peraeslibram.domain.model.Prilog
import com.peraeslibram.domain.ocr.SummonsParser
import com.peraeslibram.domain.ocr.TextRecognizer
import com.peraeslibram.domain.repository.CaseRepository
import com.peraeslibram.domain.repository.DeadlineRepository
import com.peraeslibram.domain.repository.HearingRepository
import com.peraeslibram.domain.repository.PrilogRepository
import com.peraeslibram.ui.hearings.PendingSummonsPrefill
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val attachmentDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")

@HiltViewModel
class CaseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val caseRepository: CaseRepository,
    private val hearingRepository: HearingRepository,
    private val deadlineRepository: DeadlineRepository,
    private val prilogRepository: PrilogRepository,
    private val attachmentFileStore: AttachmentFileStore,
    // `Lazy`, da gradnja ViewModel-a (a time i otvaranje ekrana predmeta) ne konstruiše
    // ML Kit prepoznavač — treba tek onome ko stvarno skenira poziv.
    private val textRecognizer: Lazy<TextRecognizer>,
    private val pendingSummonsPrefill: PendingSummonsPrefill
) : ViewModel() {

    val caseId: Long = checkNotNull(savedStateHandle.get<Long>("caseId"))

    val case: StateFlow<Case?> = caseRepository.observeById(caseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val hearings: StateFlow<List<Hearing>> = hearingRepository.observeByCase(caseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deadlines: StateFlow<List<Deadline>> = deadlineRepository.observeByCase(caseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attachments: StateFlow<List<Prilog>> = prilogRepository.observeByCase(caseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteHearing(hearing: Hearing) {
        viewModelScope.launch { hearingRepository.delete(hearing) }
    }

    fun deleteDeadline(deadline: Deadline) {
        viewModelScope.launch { deadlineRepository.delete(deadline) }
    }

    fun resolveAttachmentFile(prilog: Prilog): File = attachmentFileStore.resolve(caseId, prilog.fileName)

    fun addAttachments(pageUris: List<Uri>) {
        viewModelScope.launch {
            val timestamp = Instant.now()
            val label = timestamp.atZone(ZoneId.systemDefault()).format(attachmentDateTimeFormatter)
            val total = pageUris.size
            pageUris.forEachIndexed { index, uri ->
                val destination = attachmentFileStore.importPage(caseId, uri)
                val naziv = if (total == 1) {
                    "Prilog $label"
                } else {
                    "Prilog $label — str. ${index + 1}/$total"
                }
                prilogRepository.save(
                    Prilog(caseId = caseId, naziv = naziv, fileName = destination.name, datumKreiranja = timestamp)
                )
            }
        }
    }

    /**
     * Fotografija poziva postaje i prilog uz predmet (dokaz da je poziv uručen) i ulaz za OCR.
     * Prepoznati podaci se ostavljaju u [pendingSummonsPrefill] da ih [onReady] pokupi otvaranjem
     * forme za novo ročište — korisnik uvek mora da ih pregleda i potvrdi pre čuvanja.
     */
    fun scanSummons(pageUri: Uri, onReady: () -> Unit) {
        viewModelScope.launch {
            val timestamp = Instant.now()
            val label = timestamp.atZone(ZoneId.systemDefault()).format(attachmentDateTimeFormatter)
            val destination = attachmentFileStore.importPage(caseId, pageUri)
            prilogRepository.save(
                Prilog(caseId = caseId, naziv = "Poziv $label", fileName = destination.name, datumKreiranja = timestamp)
            )

            val text = runCatching { textRecognizer.get().recognize(pageUri) }.getOrDefault("")
            pendingSummonsPrefill.set(SummonsParser.parse(text))
            onReady()
        }
    }

    fun deleteAttachment(prilog: Prilog) {
        viewModelScope.launch { prilogRepository.delete(prilog) }
    }

    fun renameAttachment(prilog: Prilog, newName: String) {
        viewModelScope.launch { prilogRepository.rename(prilog, newName) }
    }
}
