package com.peraeslibram.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.peraeslibram.ui.attachments.AttachmentViewerScreen
import com.peraeslibram.ui.cases.CaseDetailScreen
import com.peraeslibram.ui.cases.CaseFormScreen
import com.peraeslibram.ui.cases.CaseListScreen
import com.peraeslibram.ui.dashboard.DashboardScreen
import com.peraeslibram.ui.deadlines.DeadlineFormScreen
import com.peraeslibram.ui.hearings.HearingFormScreen
import com.peraeslibram.ui.settings.HolidayManagementScreen
import com.peraeslibram.ui.settings.SettingsScreen

/** Sentinel vrednost za "novi" entitet (umesto nullable navigacionog argumenta). */
const val NEW_ID = -1L

object Routes {
    const val DASHBOARD = "dashboard"
    const val CASES = "cases"
    const val CASE_FORM = "case_form/{caseId}"
    const val CASE_DETAIL = "case_detail/{caseId}"
    const val HEARING_FORM = "hearing_form/{caseId}/{hearingId}"
    const val DEADLINE_FORM = "deadline_form/{caseId}/{deadlineId}"
    const val SETTINGS = "settings"
    const val HOLIDAYS = "holidays"
    const val ATTACHMENT_VIEWER = "attachment_viewer/{caseId}/{prilogId}"

    fun caseForm(caseId: Long = NEW_ID) = "case_form/$caseId"
    fun caseDetail(caseId: Long) = "case_detail/$caseId"
    fun hearingForm(caseId: Long, hearingId: Long = NEW_ID) = "hearing_form/$caseId/$hearingId"
    fun deadlineForm(caseId: Long, deadlineId: Long = NEW_ID) = "deadline_form/$caseId/$deadlineId"
    fun attachmentViewer(caseId: Long, prilogId: Long) = "attachment_viewer/$caseId/$prilogId"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onOpenCases = { navController.navigate(Routes.CASES) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenCase = { caseId -> navController.navigate(Routes.caseDetail(caseId)) }
            )
        }
        composable(Routes.CASES) {
            CaseListScreen(
                onOpenCase = { caseId -> navController.navigate(Routes.caseDetail(caseId)) },
                onAddCase = { navController.navigate(Routes.caseForm()) },
                onBack = { navController.popBackStack() }
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
                onOpenAttachment = { caseId, prilogId -> navController.navigate(Routes.attachmentViewer(caseId, prilogId)) }
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
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onOpenHolidays = { navController.navigate(Routes.HOLIDAYS) },
                onBack = { navController.popBackStack() }
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
