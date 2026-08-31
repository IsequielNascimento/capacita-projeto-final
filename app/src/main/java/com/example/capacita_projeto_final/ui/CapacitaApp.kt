package com.example.capacita_projeto_final.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.capacita_projeto_final.R
import com.example.capacita_projeto_final.core.AppContainer
import com.example.capacita_projeto_final.core.ViewModelFactory
import com.example.capacita_projeto_final.features.point.presentation.PointDetailScreen
import com.example.capacita_projeto_final.features.point.presentation.PointDetailViewModel
import com.example.capacita_projeto_final.features.route.presentation.RouteMapScreen
import com.example.capacita_projeto_final.features.route.presentation.RouteScreen
import com.example.capacita_projeto_final.features.route.presentation.RouteViewModel
import com.example.capacita_projeto_final.features.sync.presentation.SyncScreen
import com.example.capacita_projeto_final.features.sync.presentation.SyncViewModel
import com.example.capacita_projeto_final.features.visit.presentation.VisitScreen
import com.example.capacita_projeto_final.features.visit.presentation.VisitViewModel
import com.example.capacita_projeto_final.navigation.AppRoute
import com.example.capacita_projeto_final.navigation.AppTab
import com.example.capacita_projeto_final.navigation.rememberAppCoordinator
import com.example.capacita_projeto_final.ui.components.HigListBulletSymbol
import com.example.capacita_projeto_final.ui.components.HigMapSymbol
import com.example.capacita_projeto_final.ui.components.HigSyncSymbol
import com.example.capacita_projeto_final.ui.components.HigTabBar
import com.example.capacita_projeto_final.ui.components.HigTabItem

@Composable
fun CapacitaApp(appContainer: AppContainer) {
    val navController = rememberNavController()
    val coordinator = rememberAppCoordinator(navController)

    val routeViewModel: RouteViewModel = viewModel(
        factory = ViewModelFactory {
            RouteViewModel(appContainer.routeRepository, appContainer.visitRepository)
        },
    )
    val syncViewModel: SyncViewModel = viewModel(
        factory = ViewModelFactory {
            SyncViewModel(appContainer.visitRepository, appContainer.syncRepository)
        },
    )
    val routeState by routeViewModel.uiState.collectAsStateWithLifecycle()
    val syncState by syncViewModel.uiState.collectAsStateWithLifecycle()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selectedTab = AppTab.entries.firstOrNull { tab ->
        backStackEntry?.destination?.hierarchy?.any { it.route == tab.graphRoute } == true
    } ?: AppTab.Route
    val showsTabBar = currentRoute != AppRoute.VisitSheet

    val tabItems = listOf(
        HigTabItem(AppTab.Route.name, stringResource(R.string.tab_route)) { HigListBulletSymbol(it) },
        HigTabItem(AppTab.Map.name, stringResource(R.string.tab_map)) { HigMapSymbol(it) },
        HigTabItem(AppTab.Sync.name, stringResource(R.string.tab_sync)) { HigSyncSymbol(it) },
    )

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            NavHost(navController = navController, startDestination = AppTab.Route.graphRoute) {
                navigation(startDestination = AppTab.Route.startRoute, route = AppTab.Route.graphRoute) {
                    composable(AppTab.Route.startRoute) {
                        RouteScreen(
                            state = routeState,
                            onOpenMap = { coordinator.selectTab(AppTab.Map) },
                            onPointClick = coordinator::openPointFromRoute,
                        )
                    }
                    composable(
                        route = AppRoute.RoutePointDetail,
                        arguments = listOf(navArgument("pointId") { type = NavType.IntType }),
                    ) { entry ->
                        PointDetailDestination(appContainer, entry.arguments?.getInt("pointId"), coordinator::goBack) { pointId, reading ->
                            coordinator.openVisit(pointId, reading)
                        }
                    }
                }
                navigation(startDestination = AppTab.Map.startRoute, route = AppTab.Map.graphRoute) {
                    composable(AppTab.Map.startRoute) {
                        RouteMapScreen(
                            state = routeState,
                            onPointClick = coordinator::openPointFromMap,
                        )
                    }
                    composable(
                        route = AppRoute.MapPointDetail,
                        arguments = listOf(navArgument("pointId") { type = NavType.IntType }),
                    ) { entry ->
                        PointDetailDestination(appContainer, entry.arguments?.getInt("pointId"), coordinator::goBack) { pointId, reading ->
                            coordinator.openVisit(pointId, reading)
                        }
                    }
                }
                navigation(startDestination = AppTab.Sync.startRoute, route = AppTab.Sync.graphRoute) {
                    composable(AppTab.Sync.startRoute) {
                        SyncScreen(state = syncState, onSync = syncViewModel::synchronize)
                    }
                }
                composable(
                    route = AppRoute.VisitSheet,
                    arguments = listOf(
                        navArgument("pointId") { type = NavType.IntType },
                        navArgument("reading") { type = NavType.IntType },
                    ),
                ) { entry ->
                    val pointId = entry.arguments?.getInt("pointId") ?: return@composable
                    val reading = entry.arguments?.getInt("reading") ?: return@composable
                    val visitViewModel: VisitViewModel = viewModel(
                        factory = ViewModelFactory {
                            VisitViewModel(
                                pointId = pointId,
                                reading = reading,
                                routeRepository = appContainer.routeRepository,
                                visitRepository = appContainer.visitRepository,
                                locationProvider = appContainer.deviceLocationProvider,
                            )
                        },
                    )
                    val visitState by visitViewModel.uiState.collectAsStateWithLifecycle()
                    VisitScreen(
                        state = visitState,
                        onBack = coordinator::dismissVisit,
                        onSave = visitViewModel::saveVisit,
                        onPhotoCaptured = visitViewModel::confirmPhoto,
                        onEvidenceMessage = visitViewModel::reportEvidenceFeedback,
                        onCaptureLocation = visitViewModel::captureLocation,
                        onFinish = coordinator::finishVisit,
                    )
                }
            }
        }
        if (showsTabBar) {
            HigTabBar(
                items = tabItems,
                selectedId = selectedTab.name,
                onSelect = { id -> coordinator.selectTab(AppTab.valueOf(id)) },
            )
        }
    }
}

@Composable
private fun PointDetailDestination(
    appContainer: AppContainer,
    pointId: Int?,
    onBack: () -> Unit,
    onStartVisit: (Int, Int) -> Unit,
) {
    if (pointId == null) return
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
        onBack = onBack,
        onReadingChange = pointViewModel::updateReading,
        onStartVisit = {
            pointViewModel.prepareVisit()?.let { reading -> onStartVisit(pointId, reading) }
        },
    )
}
