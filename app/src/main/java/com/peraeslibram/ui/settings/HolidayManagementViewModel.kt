package com.peraeslibram.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.domain.model.NonWorkingDay
import com.peraeslibram.domain.model.NonWorkingDaySource
import com.peraeslibram.domain.model.NonWorkingDayType
import com.peraeslibram.domain.repository.NonWorkingDayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HolidayManagementViewModel @Inject constructor(
    private val repository: NonWorkingDayRepository
) : ViewModel() {

    val holidays: StateFlow<List<NonWorkingDay>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var newDate by mutableStateOf<LocalDate?>(null)
    var newName by mutableStateOf("")

    init {
        viewModelScope.launch {
            val year = LocalDate.now().year
            repository.ensureSeededForYear(year)
            repository.ensureSeededForYear(year + 1)
        }
    }

    fun canAdd(): Boolean = newDate != null && newName.isNotBlank()

    fun addManual() {
        val date = newDate ?: return
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.addManual(
                NonWorkingDay(
                    datum = date,
                    naziv = newName,
                    tip = NonWorkingDayType.FIKSNI,
                    izvor = NonWorkingDaySource.RUCNI_UNOS
                )
            )
            newDate = null
            newName = ""
        }
    }

    fun delete(day: NonWorkingDay) {
        viewModelScope.launch { repository.delete(day) }
    }
}
