package com.peraeslibram.ui.deadlines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.domain.model.DeadlineWithCase
import com.peraeslibram.domain.usecase.GetAllDeadlinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DeadlineListViewModel @Inject constructor(
    getAllDeadlines: GetAllDeadlinesUseCase
) : ViewModel() {

    val deadlines: StateFlow<List<DeadlineWithCase>> = getAllDeadlines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
