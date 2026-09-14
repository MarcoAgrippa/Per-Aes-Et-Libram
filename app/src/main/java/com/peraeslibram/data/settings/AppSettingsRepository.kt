package com.peraeslibram.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.peraeslibram.ui.common.Script
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

@Singleton
class AppSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scriptKey = stringPreferencesKey("script")

    val scriptFlow: Flow<Script> = context.settingsDataStore.data.map { prefs ->
        Script.entries.find { it.name == prefs[scriptKey] } ?: Script.LATIN
    }

    suspend fun setScript(script: Script) {
        context.settingsDataStore.edit { it[scriptKey] = script.name }
    }
}
