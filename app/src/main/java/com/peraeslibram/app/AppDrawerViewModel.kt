package com.peraeslibram.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.domain.repository.CaseRepository
import com.peraeslibram.domain.usecase.GetUpcomingAgendaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Brojevi u fioci (Rokovi/Predmeti) — laki, samostalni izvor da fioka ne zavisi od ekrana koji je otvorio. */
@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    caseRepository: CaseRepository,
    getUpcomingAgenda: GetUpcomingAgendaUseCase
) : ViewModel() {

    val rokoviCount: StateFlow<Int> = getUpcomingAgenda().map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val predmetiCount: StateFlow<Int> = caseRepository.observeAll().map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
