package com.peraeslibram.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peraeslibram.data.settings.AppSettingsRepository
import com.peraeslibram.ui.common.Script
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: AppSettingsRepository
) : ViewModel() {

    val script: StateFlow<Script> = settingsRepository.scriptFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Script.LATIN)

    fun onScriptChange(script: Script) {
        viewModelScope.launch { settingsRepository.setScript(script) }
    }
}
