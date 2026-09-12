package com.peraeslibram.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** Rute sa kojih je fioka dostupna — hamburger u traci i prevlačenje sa ivice. */
val TOP_LEVEL_ROUTES: Set<String> = setOf(
    Routes.DASHBOARD,
    Routes.CASES,
    Routes.DEADLINES,
    Routes.COURTS,
    Routes.SETTINGS
)

private enum class DrawerDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    DASHBOARD(Routes.DASHBOARD, "Početna", Icons.Default.Home),
    CASES(Routes.CASES, "Predmeti", Icons.Default.Business),
    DEADLINES(Routes.DEADLINES, "Rokovi", Icons.Default.HourglassBottom),
    COURTS(Routes.COURTS, "Sudovi", Icons.Default.AccountBalance)
}

@Composable
fun AppDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    ModalDrawerSheet {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Balance,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                "Per Aes Et Libram",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))

        DrawerDestination.entries.forEach { destination ->
            NavigationDrawerItem(
                label = { Text(destination.label) },
                icon = { Icon(destination.icon, contentDescription = null) },
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        NavigationDrawerItem(
            label = { Text("Podešavanja") },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            selected = currentRoute == Routes.SETTINGS,
            onClick = { onNavigate(Routes.SETTINGS) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}
