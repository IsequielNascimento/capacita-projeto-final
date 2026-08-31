package com.example.capacita_projeto_final.features.route.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capacita_projeto_final.features.route.data.RouteRepository
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.visit.data.VisitRepository
import com.example.capacita_projeto_final.features.visit.domain.Visit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface RouteUiState {
    data object Loading : RouteUiState
    data class Ready(
        val points: List<RoutePoint>,
        val latestVisits: Map<Int, Visit>,
    ) : RouteUiState
    data class Error(val message: String) : RouteUiState
}

class RouteViewModel(
    private val repository: RouteRepository,
    visitRepository: VisitRepository,
) : ViewModel() {
    val uiState: StateFlow<RouteUiState> = combine(
        repository.observeRoutePoints(),
        visitRepository.observeVisits(),
    ) { points, visits ->
        val latestVisits = visits
            .groupBy(Visit::pointId)
            .mapValues { (_, pointVisits) -> pointVisits.maxBy(Visit::capturedAt) }
        val readyState: RouteUiState = RouteUiState.Ready(points, latestVisits)
        readyState
    }
        .catch { emit(RouteUiState.Error("Não foi possível abrir a rota local.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RouteUiState.Loading)

    init {
        viewModelScope.launch {
            runCatching { repository.seedIfEmpty() }
        }
    }
}
