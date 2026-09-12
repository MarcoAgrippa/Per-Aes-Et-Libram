package com.peraeslibram.ui.courts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.app.NEW_ID
import com.peraeslibram.domain.model.Court
import com.peraeslibram.domain.repository.CourtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CourtFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val courtRepository: CourtRepository
) : ViewModel() {

    private val courtId: Long = savedStateHandle.get<Long>("courtId") ?: NEW_ID

    var naziv by mutableStateOf("")
    var adresa by mutableStateOf("")
    var telefon by mutableStateOf("")
    var email by mutableStateOf("")
    var napomena by mutableStateOf("")

    private var existing: Court? = null

    init {
        if (courtId != NEW_ID) {
            viewModelScope.launch {
                courtRepository.getById(courtId)?.let { court ->
                    existing = court
                    naziv = court.naziv
                    adresa = court.adresa.orEmpty()
                    telefon = court.telefon.orEmpty()
                    email = court.email.orEmpty()
                    napomena = court.napomena.orEmpty()
                }
            }
        }
    }

    fun canSave(): Boolean = naziv.isNotBlank()

    fun save(onSaved: () -> Unit) {
        if (!canSave()) return
        viewModelScope.launch {
            courtRepository.save(
                Court(
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
