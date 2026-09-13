package com.peraeslibram.ui.notaries

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.app.NEW_ID
import com.peraeslibram.domain.model.Notary
import com.peraeslibram.domain.repository.NotaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class NotaryFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notaryRepository: NotaryRepository
) : ViewModel() {

    private val notaryId: Long = savedStateHandle.get<Long>("notaryId") ?: NEW_ID

    var naziv by mutableStateOf("")
    var adresa by mutableStateOf("")
    var telefon by mutableStateOf("")
    var email by mutableStateOf("")
    var napomena by mutableStateOf("")

    private var existing: Notary? = null

    init {
        if (notaryId != NEW_ID) {
            viewModelScope.launch {
                notaryRepository.getById(notaryId)?.let { notary ->
                    existing = notary
                    naziv = notary.naziv
                    adresa = notary.adresa.orEmpty()
                    telefon = notary.telefon.orEmpty()
                    email = notary.email.orEmpty()
                    napomena = notary.napomena.orEmpty()
                }
            }
        }
    }

    fun canSave(): Boolean = naziv.isNotBlank()

    fun save(onSaved: () -> Unit) {
        if (!canSave()) return
        viewModelScope.launch {
            notaryRepository.save(
                Notary(
                    id = existing?.id ?: 0,
                    naziv = naziv,
                    adresa = adresa.ifBlank { null },
                    telefon = telefon.ifBlank { null },
                    email = email.ifBlank { null },
                    napomena = napomena.ifBlank { null },
                    datumKreiranja = existing?.datumKreiranja ?: Instant.now(),
                    datumIzmene = Instant.now()
                )
            )
            onSaved()
        }
    }
}
