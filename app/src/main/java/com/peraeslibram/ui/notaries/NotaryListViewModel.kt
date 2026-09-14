package com.peraeslibram.ui.notaries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.domain.model.Notary
import com.peraeslibram.domain.repository.NotaryRepository
import com.peraeslibram.ui.common.matchesQuery
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
class NotaryListViewModel @Inject constructor(
    private val notaryRepository: NotaryRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = searchQuery.asStateFlow()

    val notaries: StateFlow<List<Notary>> = combine(
        notaryRepository.observeAll(),
        searchQuery
    ) { notaries, query ->
        if (query.isBlank()) {
            notaries
        } else {
            notaries.filter { notary ->
                notary.naziv.matchesQuery(query) ||
                    notary.adresa?.matchesQuery(query) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { notaryRepository.ensureSeeded() }
    }

    fun onQueryChange(value: String) {
        searchQuery.value = value
    }

    fun delete(notary: Notary) {
        viewModelScope.launch { notaryRepository.delete(notary) }
    }
}
