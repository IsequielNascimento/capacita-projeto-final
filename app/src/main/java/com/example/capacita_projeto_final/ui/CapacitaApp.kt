package com.example.capacita_projeto_final.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.capacita_projeto_final.core.AppContainer
import com.example.capacita_projeto_final.core.ViewModelFactory
import com.example.capacita_projeto_final.features.point.presentation.PointDetailScreen
import com.example.capacita_projeto_final.features.point.presentation.PointDetailViewModel
import com.example.capacita_projeto_final.features.route.presentation.RouteScreen
import com.example.capacita_projeto_final.features.route.presentation.RouteViewModel
import com.example.capacita_projeto_final.features.visit.presentation.VisitScreen
import com.example.capacita_projeto_final.features.visit.presentation.VisitViewModel

private const val RouteDestination = "route"
private const val PointDestination = "point/{pointId}"
private const val VisitDestination = "visit/{pointId}/{reading}"

@Composable
fun CapacitaApp(appContainer: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RouteDestination) {
        composable(RouteDestination) {
            val routeViewModel: RouteViewModel = viewModel(
                factory = ViewModelFactory {
                    RouteViewModel(appContainer.routeRepository, appContainer.visitRepository)
                },
            )
            val state by routeViewModel.uiState.collectAsStateWithLifecycle()
            RouteScreen(
                state = state,
                onPointClick = { pointId -> navController.navigate("point/$pointId") },
            )
        }
        composable(
            route = PointDestination,
            arguments = listOf(navArgument("pointId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val pointId = backStackEntry.arguments?.getInt("pointId") ?: return@composable
            val pointViewModel: PointDetailViewModel = viewModel(
                factory = ViewModelFactory {
                    PointDetailViewModel(
                        pointId = pointId,
                        routeRepository = appContainer.routeRepository,
                        visitRepository = appContainer.visitRepository,
                    )
                },
            )
            val state by pointViewModel.uiState.collectAsStateWithLifecycle()
            PointDetailScreen(
                state = state,
                onBack = navController::popBackStack,
                onReadingChange = pointViewModel::updateReading,
                onStartVisit = {
                    pointViewModel.prepareVisit()?.let { reading ->
                        navController.navigate("visit/$pointId/$reading")
                    }
                },
            )
        }
        composable(
            route = VisitDestination,
            arguments = listOf(
                navArgument("pointId") { type = NavType.IntType },
                navArgument("reading") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val pointId = backStackEntry.arguments?.getInt("pointId") ?: return@composable
            val reading = backStackEntry.arguments?.getInt("reading") ?: return@composable
            val visitViewModel: VisitViewModel = viewModel(
                factory = ViewModelFactory {
                    VisitViewModel(
                        pointId = pointId,
                        reading = reading,
                        routeRepository = appContainer.routeRepository,
                        visitRepository = appContainer.visitRepository,
                    )
                },
            )
            val state by visitViewModel.uiState.collectAsStateWithLifecycle()
            VisitScreen(
                state = state,
                onBack = navController::popBackStack,
                onSave = visitViewModel::saveVisit,
                onFinish = {
                    navController.popBackStack(RouteDestination, inclusive = false)
                },
            )
        }
    }
}
