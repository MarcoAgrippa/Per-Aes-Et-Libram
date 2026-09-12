package com.peraeslibram.ui.cases

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.app.NEW_ID
import com.peraeslibram.domain.model.Case
import com.peraeslibram.domain.model.CaseStatus
import com.peraeslibram.domain.model.TipPostupka
import com.peraeslibram.domain.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CaseFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val caseRepository: CaseRepository
) : ViewModel() {

    private val caseId: Long = savedStateHandle.get<Long>("caseId") ?: NEW_ID

    var naziv by mutableStateOf("")
    var brojPredmeta by mutableStateOf("")
    var klijentIme by mutableStateOf("")
    var klijentKontakt by mutableStateOf("")
    var sud by mutableStateOf("")
    var tipPostupka by mutableStateOf(TipPostupka.PARNICNI)
    var napomena by mutableStateOf("")
    var status by mutableStateOf(CaseStatus.AKTIVAN)

    private var existing: Case? = null

    init {
        if (caseId != NEW_ID) {
            viewModelScope.launch {
                caseRepository.getById(caseId)?.let { case ->
                    existing = case
                    naziv = case.naziv
                    brojPredmeta = case.brojPredmeta.orEmpty()
                    klijentIme = case.klijentIme
                    klijentKontakt = case.klijentKontakt.orEmpty()
                    sud = case.sud.orEmpty()
                    tipPostupka = case.tipPostupka
                    napomena = case.napomena.orEmpty()
                    status = case.status
                }
            }
        }
    }

    fun canSave(): Boolean = naziv.isNotBlank() && klijentIme.isNotBlank()

    fun save(onSaved: (Long) -> Unit) {
        if (!canSave()) return
        viewModelScope.launch {
            val case = Case(
                id = existing?.id ?: 0,
                naziv = naziv,
                brojPredmeta = brojPredmeta.ifBlank { null },
                klijentIme = klijentIme,
                klijentKontakt = klijentKontakt.ifBlank { null },
                sud = sud.ifBlank { null },
                tipPostupka = tipPostupka,
                napomena = napomena.ifBlank { null },
                status = status,
                datumKreiranja = existing?.datumKreiranja ?: Instant.now(),
                datumIzmene = Instant.now()
            )
            val id = caseRepository.save(case)
            onSaved(id)
        }
    }
}
