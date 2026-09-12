package com.peraeslibram.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.domain.model.AgendaItem
import com.peraeslibram.domain.usecase.GetUpcomingAgendaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getUpcomingAgenda: GetUpcomingAgendaUseCase
) : ViewModel() {

    val agenda: StateFlow<List<AgendaItem>> = getUpcomingAgenda()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
