package com.example.capacita_projeto_final.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

// MARK: - Coordinator

class AppCoordinator(private val navController: NavHostController) {

    fun selectTab(tab: AppTab) {
        navController.navigate(tab.graphRoute) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun openPointFromRoute(pointId: Int) {
        navController.navigate(AppRoute.routePointDetail(pointId), singleTop())
    }

    fun openPointFromMap(pointId: Int) {
        navController.navigate(AppRoute.mapPointDetail(pointId), singleTop())
    }

    fun openVisit(pointId: Int, reading: Int) {
        navController.navigate(AppRoute.visitSheet(pointId, reading), singleTop())
    }

    fun dismissVisit() {
        navController.popBackStack()
    }

    fun finishVisit() {
        navController.popBackStack()
        navController.popBackStack()
    }

    fun goBack() {
        navController.popBackStack()
    }

    private fun singleTop(): NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
}

@Composable
fun rememberAppCoordinator(navController: NavHostController): AppCoordinator =
    remember(navController) { AppCoordinator(navController) }
