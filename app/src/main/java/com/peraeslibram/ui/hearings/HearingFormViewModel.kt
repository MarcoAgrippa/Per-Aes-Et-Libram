package com.peraeslibram.ui.hearings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.app.NEW_ID
import com.peraeslibram.domain.model.Hearing
import com.peraeslibram.domain.model.HearingStatus
import com.peraeslibram.domain.repository.HearingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.launch

data class ReminderOption(val minutesBefore: Long, val label: String)

val HEARING_REMINDER_OPTIONS = listOf(
    ReminderOption(4320, "3 dana pre"),
    ReminderOption(1440, "1 dan pre"),
    ReminderOption(120, "2 sata pre"),
    ReminderOption(30, "30 minuta pre")
)

/**
 * Uobičajene vrste ročišta u domaćoj sudskoj praksi, obuhvataju parnični, krivični,
 * prekršajni, upravni i izvršni postupak. "Ostalo" ostaje kao izlaz za slučajeve koji ne
 * odgovaraju nijednoj od ponuđenih vrednosti.
 */
val HEARING_TYPE_OPTIONS = listOf(
    "Pripremno ročište",
    "Ročište za glavnu raspravu",
    "Glavni pretres",
    "Ročište za izjašnjenje o krivici",
    "Ročište za saslušanje svedoka",
    "Ročište za veštačenje",
    "Ročište za poravnanje",
    "Ročište za objavljivanje presude",
    "Usmena rasprava (upravni spor)",
    "Ročište u prekršajnom postupku",
    "Ročište u izvršnom postupku",
    "Ročište za javnu prodaju",
    "Ročište pred drugostepenim sudom",
    "Ostalo"
)

@HiltViewModel
class HearingFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val hearingRepository: HearingRepository
) : ViewModel() {

    val caseId: Long = checkNotNull(savedStateHandle.get<Long>("caseId"))
    private val hearingId: Long = savedStateHandle.get<Long>("hearingId") ?: NEW_ID

    var datum by mutableStateOf<LocalDate?>(null)
    var vreme by mutableStateOf<LocalTime?>(null)
    var sud by mutableStateOf("")
    var sudnica by mutableStateOf("")
    var tipRocista by mutableStateOf("")
    var napomena by mutableStateOf("")
    var reminderOffsets by mutableStateOf(setOf(1440L))

    private var existing: Hearing? = null

    init {
        if (hearingId != NEW_ID) {
            viewModelScope.launch {
                hearingRepository.getById(hearingId)?.let { h ->
                    existing = h
                    datum = h.datumVreme.toLocalDate()
                    vreme = h.datumVreme.toLocalTime()
                    sud = h.sud.orEmpty()
                    sudnica = h.sudnica.orEmpty()
                    tipRocista = h.tipRocista.orEmpty()
                    napomena = h.napomena.orEmpty()
                }
            }
        }
    }

    fun toggleReminder(minutes: Long) {
        reminderOffsets = if (minutes in reminderOffsets) reminderOffsets - minutes else reminderOffsets + minutes
    }

    fun canSave(): Boolean = datum != null && vreme != null

    fun save(onSaved: () -> Unit) {
        val d = datum ?: return
        val t = vreme ?: return
        viewModelScope.launch {
            val hearing = Hearing(
                id = existing?.id ?: 0,
                caseId = caseId,
                datumVreme = LocalDateTime.of(d, t),
                sud = sud.ifBlank { null },
                sudnica = sudnica.ifBlank { null },
                tipRocista = tipRocista.ifBlank { null },
                napomena = napomena.ifBlank { null },
                status = existing?.status ?: HearingStatus.ZAKAZANO,
                datumKreiranja = existing?.datumKreiranja ?: Instant.now(),
                datumIzmene = Instant.now()
            )
            hearingRepository.save(hearing, reminderOffsets.toList())
            onSaved()
        }
    }
}
