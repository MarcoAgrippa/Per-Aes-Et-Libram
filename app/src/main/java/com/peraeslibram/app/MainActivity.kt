package com.peraeslibram.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.peraeslibram.data.notification.ReminderReceiver
import com.peraeslibram.data.settings.AppSettingsRepository
import com.peraeslibram.ui.common.LocalScript
import com.peraeslibram.ui.common.Script
import com.peraeslibram.ui.theme.PerAesEtLibramTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: AppSettingsRepository

    private var deepLinkCaseId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deepLinkCaseId = extractDeepLinkCaseId(intent)
        setContent {
            val script by settingsRepository.scriptFlow.collectAsState(initial = Script.LATIN)
            CompositionLocalProvider(LocalScript provides script) {
                PerAesEtLibramTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        AppNavGraph(
                            deepLinkCaseId = deepLinkCaseId,
                            onDeepLinkConsumed = { deepLinkCaseId = null }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkCaseId = extractDeepLinkCaseId(intent)
    }

    private fun extractDeepLinkCaseId(intent: Intent?): Long? {
        val caseId = intent?.getLongExtra(ReminderReceiver.EXTRA_DEEPLINK_CASE_ID, -1L) ?: -1L
        return caseId.takeIf { it != -1L }
    }
}
