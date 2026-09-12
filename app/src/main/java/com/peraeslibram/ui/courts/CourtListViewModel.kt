package com.peraeslibram.ui.courts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.domain.model.Court
import com.peraeslibram.domain.repository.CourtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CourtListViewModel @Inject constructor(
    private val courtRepository: CourtRepository
) : ViewModel() {

    val courts: StateFlow<List<Court>> = courtRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(court: Court) {
        viewModelScope.launch { courtRepository.delete(court) }
    }
}
