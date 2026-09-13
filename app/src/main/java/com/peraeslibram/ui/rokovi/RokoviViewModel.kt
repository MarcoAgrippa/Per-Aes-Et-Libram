package com.peraeslibram.ui.rokovi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.domain.model.AgendaItem
import com.peraeslibram.domain.usecase.GetUpcomingAgendaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Filter za objedinjenu agendu — sve stavke, samo ročišta, ili samo rokovi. */
enum class RokoviFilter { SVE, ROCISTA, ROKOVI }

@HiltViewModel
class RokoviViewModel @Inject constructor(
    getUpcomingAgenda: GetUpcomingAgendaUseCase
) : ViewModel() {

    private val agenda: StateFlow<List<AgendaItem>> = getUpcomingAgenda()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filter = MutableStateFlow(RokoviFilter.SVE)
    val filter: StateFlow<RokoviFilter> = _filter.asStateFlow()

    val visibleAgenda: StateFlow<List<AgendaItem>> = combine(agenda, _filter) { items, filter ->
        when (filter) {
            RokoviFilter.SVE -> items
            RokoviFilter.ROCISTA -> items.filterIsInstance<AgendaItem.HearingItem>()
            RokoviFilter.ROKOVI -> items.filterIsInstance<AgendaItem.DeadlineItem>()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(value: RokoviFilter) {
        _filter.value = value
    }
}
