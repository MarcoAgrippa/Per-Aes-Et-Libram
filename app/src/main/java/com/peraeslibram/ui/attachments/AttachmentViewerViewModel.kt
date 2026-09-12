package com.peraeslibram.ui.attachments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.data.local.storage.AttachmentFileStore
import com.peraeslibram.domain.model.Prilog
import com.peraeslibram.domain.repository.PrilogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AttachmentViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val prilogRepository: PrilogRepository,
    private val attachmentFileStore: AttachmentFileStore
) : ViewModel() {

    private val caseId: Long = checkNotNull(savedStateHandle.get<Long>("caseId"))
    private val prilogId: Long = checkNotNull(savedStateHandle.get<Long>("prilogId"))

    val prilog: StateFlow<Prilog?> = prilogRepository.observeByCase(caseId)
        .map { list -> list.find { it.id == prilogId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun resolveFile(prilog: Prilog): File = attachmentFileStore.resolve(caseId, prilog.fileName)

    fun rename(newName: String) {
        val current = prilog.value ?: return
        viewModelScope.launch { prilogRepository.rename(current, newName) }
    }

    fun delete(onDeleted: () -> Unit) {
        val current = prilog.value ?: return
        viewModelScope.launch {
            prilogRepository.delete(current)
            onDeleted()
        }
    }
}
