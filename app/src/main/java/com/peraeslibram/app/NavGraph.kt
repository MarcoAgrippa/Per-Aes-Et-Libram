package com.peraeslibram.app

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.peraeslibram.ui.attachments.AttachmentViewerScreen
import com.peraeslibram.ui.cases.CaseDetailScreen
import com.peraeslibram.ui.cases.CaseFormScreen
import com.peraeslibram.ui.cases.CaseListScreen
import com.peraeslibram.ui.courts.CourtFormScreen
import com.peraeslibram.ui.courts.CourtListScreen
import com.peraeslibram.ui.deadlines.DeadlineFormScreen
import com.peraeslibram.ui.hearings.HearingFormScreen
import com.peraeslibram.ui.notaries.NotaryFormScreen
import com.peraeslibram.ui.notaries.NotaryListScreen
import com.peraeslibram.ui.rokovi.RokoviScreen
import com.peraeslibram.ui.settings.HolidayManagementScreen
import com.peraeslibram.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

/** Sentinel vrednost za "novi" entitet (umesto nullable navigacionog argumenta). */
const val NEW_ID = -1L

object Routes {
    const val ROKOVI = "rokovi"
    const val CASES = "cases"
    const val CASE_FORM = "case_form/{caseId}"
    const val CASE_DETAIL = "case_detail/{caseId}"
    const val HEARING_FORM = "hearing_form/{caseId}/{hearingId}"
    const val DEADLINE_FORM = "deadline_form/{caseId}/{deadlineId}"
    const val COURTS = "courts"
    const val COURT_FORM = "court_form/{courtId}"
    const val NOTARIES = "notaries"
    const val NOTARY_FORM = "notary_form/{notaryId}"
    const val SETTINGS = "settings"
    const val HOLIDAYS = "holidays"
    const val ATTACHMENT_VIEWER = "attachment_viewer/{caseId}/{prilogId}"

    fun caseForm(caseId: Long = NEW_ID) = "case_form/$caseId"
    fun caseDetail(caseId: Long) = "case_detail/$caseId"
    fun hearingForm(caseId: Long, hearingId: Long = NEW_ID) = "hearing_form/$caseId/$hearingId"
    fun deadlineForm(caseId: Long, deadlineId: Long = NEW_ID) = "deadline_form/$caseId/$deadlineId"
    fun courtForm(courtId: Long = NEW_ID) = "court_form/$courtId"
    fun notaryForm(notaryId: Long = NEW_ID) = "notary_form/$notaryId"
    fun attachmentViewer(caseId: Long, prilogId: Long) = "attachment_viewer/$caseId/$prilogId"
}

@Composable
fun AppNavGraph(deepLinkCaseId: Long? = null, onDeepLinkConsumed: () -> Unit = {}) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = currentRoute in TOP_LEVEL_ROUTES

    // Podsetnik/notifikacija otvara aplikaciju direktno na predmetu na koji se odnosi.
    LaunchedEffect(deepLinkCaseId) {
        val caseId = deepLinkCaseId ?: return@LaunchedEffect
        navController.navigate(Routes.caseDetail(caseId)) { launchSingleTop = true }
        onDeepLinkConsumed()
    }

    // Material3 1.2.1 nema ugrađen BackHandler u ModalNavigationDrawer — bez ovoga bi sistemsko
    // "nazad" sa otvorenom fiokom izašlo iz aplikacije umesto da je zatvori.
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    val openDrawer: () -> Unit = remember(scope, drawerState) {
        { scope.launch { drawerState.open() }; Unit }
    }

    val navigateTopLevel: (String) -> Unit = remember(scope, drawerState, navController) {
        { route ->
            scope.launch { drawerState.close() }
            if (route != navController.currentDestination?.route) {
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isTopLevel || drawerState.isOpen,
        drawerContent = { AppDrawerContent(currentRoute = currentRoute, onNavigate = navigateTopLevel) }
    ) {
        NavHost(navController = navController, startDestination = Routes.ROKOVI) {
            composable(Routes.ROKOVI) {
                RokoviScreen(
                    onOpenDrawer = openDrawer,
                    onAddCase = { navController.navigate(Routes.caseForm()) },
                    onOpenCase = { caseId -> navController.navigate(Routes.caseDetail(caseId)) }
                )
            }
            composable(Routes.CASES) {
                CaseListScreen(
                    onOpenDrawer = openDrawer,
                    onOpenCase = { caseId -> navController.navigate(Routes.caseDetail(caseId)) },
                    onAddCase = { navController.navigate(Routes.caseForm()) }
                )
            }
            composable(
                Routes.CASE_FORM,
                arguments = listOf(navArgument("caseId") { type = NavType.LongType })
            ) {
                CaseFormScreen(
                    onSaved = { caseId ->
                        navController.popBackStack()
                        navController.navigate(Routes.caseDetail(caseId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                Routes.CASE_DETAIL,
                arguments = listOf(navArgument("caseId") { type = NavType.LongType })
            ) {
                CaseDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEditCase = { caseId -> navController.navigate(Routes.caseForm(caseId)) },
                    onAddHearing = { caseId -> navController.navigate(Routes.hearingForm(caseId)) },
                    onEditHearing = { caseId, hearingId -> navController.navigate(Routes.hearingForm(caseId, hearingId)) },
                    onAddDeadline = { caseId -> navController.navigate(Routes.deadlineForm(caseId)) },
                    onEditDeadline = { caseId, deadlineId -> navController.navigate(Routes.deadlineForm(caseId, deadlineId)) },
                    onOpenAttachment = { caseId, prilogId -> navController.navigate(Routes.attachmentViewer(caseId, prilogId)) },
                    onScanSummons = { caseId -> navController.navigate(Routes.hearingForm(caseId)) }
                )
            }
            composable(
                Routes.HEARING_FORM,
                arguments = listOf(
                    navArgument("caseId") { type = NavType.LongType },
                    navArgument("hearingId") { type = NavType.LongType }
                )
            ) {
                HearingFormScreen(
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                Routes.DEADLINE_FORM,
                arguments = listOf(
                    navArgument("caseId") { type = NavType.LongType },
                    navArgument("deadlineId") { type = NavType.LongType }
                )
            ) {
                DeadlineFormScreen(
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.COURTS) {
                CourtListScreen(
                    onOpenDrawer = openDrawer,
                    onAddCourt = { navController.navigate(Routes.courtForm()) },
                    onEditCourt = { courtId -> navController.navigate(Routes.courtForm(courtId)) }
                )
            }
            composable(
                Routes.COURT_FORM,
                arguments = listOf(navArgument("courtId") { type = NavType.LongType })
            ) {
                CourtFormScreen(
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.NOTARIES) {
                NotaryListScreen(
                    onOpenDrawer = openDrawer,
                    onAddNotary = { navController.navigate(Routes.notaryForm()) },
                    onEditNotary = { notaryId -> navController.navigate(Routes.notaryForm(notaryId)) }
                )
            }
            composable(
                Routes.NOTARY_FORM,
                arguments = listOf(navArgument("notaryId") { type = NavType.LongType })
            ) {
                NotaryFormScreen(
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onOpenDrawer = openDrawer,
                    onOpenHolidays = { navController.navigate(Routes.HOLIDAYS) }
                )
            }
            composable(Routes.HOLIDAYS) {
                HolidayManagementScreen(onBack = { navController.popBackStack() })
            }
            composable(
                Routes.ATTACHMENT_VIEWER,
                arguments = listOf(
                    navArgument("caseId") { type = NavType.LongType },
                    navArgument("prilogId") { type = NavType.LongType }
                )
            ) {
                AttachmentViewerScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
