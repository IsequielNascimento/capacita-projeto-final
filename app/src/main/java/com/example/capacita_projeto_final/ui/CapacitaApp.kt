package com.example.capacita_projeto_final.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.capacita_projeto_final.core.AppContainer
import com.example.capacita_projeto_final.core.ViewModelFactory
import com.example.capacita_projeto_final.features.route.presentation.RouteScreen
import com.example.capacita_projeto_final.features.route.presentation.RouteViewModel

private const val RouteDestination = "route"
private const val PointDestination = "point/{pointId}"

@Composable
fun CapacitaApp(appContainer: AppContainer) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RouteDestination,
    ) {
        composable(RouteDestination) {
            val routeViewModel: RouteViewModel = viewModel(
                factory = ViewModelFactory { RouteViewModel(appContainer.routeRepository) },
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
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Ponto $pointId", style = MaterialTheme.typography.headlineSmall)
                Text("Detalhes do atendimento", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
