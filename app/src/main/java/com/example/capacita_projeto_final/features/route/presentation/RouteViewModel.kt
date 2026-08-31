package com.example.capacita_projeto_final.features.route.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capacita_projeto_final.features.route.data.RouteRepository
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface RouteUiState {
    data object Loading : RouteUiState
    data class Ready(val points: List<RoutePoint>) : RouteUiState
    data class Error(val message: String) : RouteUiState
}

class RouteViewModel(
    private val repository: RouteRepository,
) : ViewModel() {
    val uiState: StateFlow<RouteUiState> = repository.observeRoutePoints()
        .map<List<RoutePoint>, RouteUiState> { RouteUiState.Ready(it) }
        .catch { emit(RouteUiState.Error("Não foi possível abrir a rota local.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RouteUiState.Loading)

    init {
        viewModelScope.launch {
            runCatching { repository.seedIfEmpty() }
        }
    }
}
