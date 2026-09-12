package com.peraeslibram.ui.courts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.domain.model.Court
import com.peraeslibram.domain.repository.CourtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CourtListViewModel @Inject constructor(
    private val courtRepository: CourtRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = searchQuery.asStateFlow()

    val courts: StateFlow<List<Court>> = combine(
        courtRepository.observeAll(),
        searchQuery
    ) { courts, query ->
        if (query.isBlank()) {
            courts
        } else {
            courts.filter { court ->
                court.naziv.contains(query, ignoreCase = true) ||
                    court.adresa?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { courtRepository.ensureSeeded() }
    }

    fun onQueryChange(value: String) {
        searchQuery.value = value
    }

    fun delete(court: Court) {
        viewModelScope.launch { courtRepository.delete(court) }
    }
}
