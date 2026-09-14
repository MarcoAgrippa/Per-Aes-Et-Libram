package com.peraeslibram.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.ui.common.Text

/** Rute sa kojih je fioka dostupna — hamburger u traci i prevlačenje sa ivice. */
val TOP_LEVEL_ROUTES: Set<String> = setOf(
    Routes.ROKOVI,
    Routes.CASES,
    Routes.COURTS,
    Routes.NOTARIES,
    Routes.SETTINGS
)

private enum class DrawerDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    ROKOVI(Routes.ROKOVI, "Rokovi", Icons.Default.HourglassBottom),
    CASES(Routes.CASES, "Predmeti", Icons.Default.Business),
    COURTS(Routes.COURTS, "Sudovi", Icons.Default.AccountBalance),
    NOTARIES(Routes.NOTARIES, "Javni beležnici", Icons.Default.Description)
}

@Composable
fun AppDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    viewModel: AppDrawerViewModel = hiltViewModel()
) {
    val rokoviCount by viewModel.rokoviCount.collectAsState()
    val predmetiCount by viewModel.predmetiCount.collectAsState()

    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.Balance,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                "Per Aes Et Libram",
                style = MaterialTheme.typography.titleLarge
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(Modifier.height(6.dp))
        DrawerRow(
            icon = DrawerDestination.ROKOVI.icon,
            label = DrawerDestination.ROKOVI.label,
            badge = if (rokoviCount > 0) rokoviCount.toString() else null,
            selected = currentRoute == DrawerDestination.ROKOVI.route,
            onClick = { onNavigate(DrawerDestination.ROKOVI.route) }
        )
        DrawerRow(
            icon = DrawerDestination.CASES.icon,
            label = DrawerDestination.CASES.label,
            badge = if (predmetiCount > 0) predmetiCount.toString() else null,
            selected = currentRoute == DrawerDestination.CASES.route,
            onClick = { onNavigate(DrawerDestination.CASES.route) }
        )
        DrawerRow(
            icon = DrawerDestination.COURTS.icon,
            label = DrawerDestination.COURTS.label,
            selected = currentRoute == DrawerDestination.COURTS.route,
            onClick = { onNavigate(DrawerDestination.COURTS.route) }
        )
        DrawerRow(
            icon = DrawerDestination.NOTARIES.icon,
            label = DrawerDestination.NOTARIES.label,
            selected = currentRoute == DrawerDestination.NOTARIES.route,
            onClick = { onNavigate(DrawerDestination.NOTARIES.route) }
        )

        Spacer(Modifier.height(2.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        DrawerRow(
            icon = Icons.Default.Settings,
            label = "Podešavanja",
            selected = currentRoute == Routes.SETTINGS,
            onClick = { onNavigate(Routes.SETTINGS) }
        )
    }
}

/** Tih red bez ispune — aktivna stavka dobija samo levu ivicu i akcentnu boju, ne pilulu. */
@Composable
private fun DrawerRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 22.dp, end = 22.dp, top = 13.dp, bottom = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.width(2.dp).height(20.dp).background(if (selected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent))
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.titleSmall, color = contentColor, modifier = Modifier.weight(1f))
        badge?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
