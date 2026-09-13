package com.peraeslibram.ui.deadlines

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.app.NEW_ID
import com.peraeslibram.domain.calculator.DeadlineCalculationResult
import com.peraeslibram.domain.model.DataSource
import com.peraeslibram.domain.model.Deadline
import com.peraeslibram.domain.model.DeadlineRule
import com.peraeslibram.domain.model.DeadlineStatus
import com.peraeslibram.domain.model.TipPostupka
import com.peraeslibram.domain.model.TipRadnje
import com.peraeslibram.domain.repository.DeadlineRepository
import com.peraeslibram.domain.repository.DeadlineRuleRepository
import com.peraeslibram.domain.usecase.CalculateDeadlineDueDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val DEADLINE_REMINDER_OPTIONS = listOf(
    4320L to "3 dana pre roka",
    1440L to "1 dan pre roka",
    0L to "Na dan isteka roka"
)

@HiltViewModel
class DeadlineFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deadlineRepository: DeadlineRepository,
    deadlineRuleRepository: DeadlineRuleRepository,
    private val calculateDueDate: CalculateDeadlineDueDateUseCase
) : ViewModel() {

    val caseId: Long = checkNotNull(savedStateHandle.get<Long>("caseId"))
    private val deadlineId: Long = savedStateHandle.get<Long>("deadlineId") ?: NEW_ID

    val rules: StateFlow<List<DeadlineRule>> = deadlineRuleRepository.observeActiveRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var selectedRule by mutableStateOf<DeadlineRule?>(null)
        private set

    /** Postavljeno iz postojećeg roka dok se lista pravila ne učita, da bi se odabrao ispravan red. */
    var initialTipRadnje by mutableStateOf<TipRadnje?>(null)
        private set

    var customBrojDana by mutableStateOf("")
        private set
    var customOpis by mutableStateOf("")
    var datumOkidaca by mutableStateOf<LocalDate?>(null)
        private set
    var napomena by mutableStateOf("")
    var reminderOffsets by mutableStateOf(setOf(4320L, 1440L))

    var calculationResult by mutableStateOf<DeadlineCalculationResult?>(null)
        private set

    private var existing: Deadline? = null

    init {
        if (deadlineId != NEW_ID) {
            viewModelScope.launch {
                deadlineRepository.getById(deadlineId)?.let { d ->
                    existing = d
                    datumOkidaca = d.datumOkidaca
                    napomena = d.napomena.orEmpty()
                    customBrojDana = d.brojDana.toString()
                    customOpis = d.opisCustomRadnje.orEmpty()
                    initialTipRadnje = d.tipRadnje
                    recalculate()
                }
            }
        }
    }

    fun onRuleSelected(rule: DeadlineRule) {
        selectedRule = rule
        if (rule.brojDana != null) {
            customBrojDana = rule.brojDana.toString()
        }
        recalculate()
    }

    fun onDatumOkidacaChanged(date: LocalDate) {
        datumOkidaca = date
        recalculate()
    }

    fun onCustomBrojDanaChanged(value: String) {
        customBrojDana = value.filter { it.isDigit() }
        recalculate()
    }

    fun toggleReminder(minutes: Long) {
        reminderOffsets = if (minutes in reminderOffsets) reminderOffsets - minutes else reminderOffsets + minutes
    }

    private fun recalculate() {
        val date = datumOkidaca
        val days = customBrojDana.toIntOrNull()
        if (date == null || days == null || days <= 0) {
            calculationResult = null
            return
        }
        viewModelScope.launch {
            calculationResult = calculateDueDate(date, days)
        }
    }

    /** Broj dana mora biti pozitivan — [DeadlineCalculator] to zahteva, ovde se provera radi rano da polje javi grešku umesto da obračun baci izuzetak. */
    fun brojDanaError(): String? {
        val days = customBrojDana.toIntOrNull()
        return if (customBrojDana.isNotBlank() && (days == null || days <= 0)) {
            "Broj dana mora biti pozitivan broj."
        } else {
            null
        }
    }

    fun canSave(): Boolean =
        datumOkidaca != null && customBrojDana.toIntOrNull()?.let { it > 0 } == true && selectedRule != null && calculationResult != null

    fun save(onSaved: () -> Unit) {
        val rule = selectedRule ?: return
        val date = datumOkidaca ?: return
        val days = customBrojDana.toIntOrNull() ?: return
        val result = calculationResult ?: return
        val isCustom = rule.tipRadnje == TipRadnje.CUSTOM_GENERICKI

        viewModelScope.launch {
            val deadline = Deadline(
                id = existing?.id ?: 0,
                caseId = caseId,
                tipPostupka = rule.tipPostupka ?: TipPostupka.DRUGO,
                tipRadnje = rule.tipRadnje,
                nazivRadnjePrikaz = if (isCustom) customOpis.ifBlank { rule.nazivPrikaz } else rule.nazivPrikaz,
                opisCustomRadnje = if (isCustom) customOpis.ifBlank { null } else null,
                datumOkidaca = date,
                brojDana = days,
                izracunatiKrajnjiDatum = result.finalDueDate,
                originalniKrajnjiDatum = if (result.wasAdjusted) result.rawDueDate else null,
                krajnjiDatumPomeren = result.wasAdjusted,
                status = existing?.status ?: DeadlineStatus.AKTIVAN,
                izvor = existing?.izvor ?: DataSource.MANUELNO,
                napomena = napomena.ifBlank { null },
                datumKreiranja = existing?.datumKreiranja ?: Instant.now(),
                datumIzmene = Instant.now()
            )
            deadlineRepository.save(deadline, reminderOffsets.toList())
            onSaved()
        }
    }
}
