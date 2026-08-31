package com.example.capacita_projeto_final.features.visit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capacita_projeto_final.features.route.data.RouteRepository
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.visit.data.VisitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface VisitUiState {
    data object Loading : VisitUiState
    data class Ready(val point: RoutePoint, val reading: Int, val saving: Boolean) : VisitUiState
    data class Saved(val point: RoutePoint, val reading: Int) : VisitUiState
    data class Error(val message: String) : VisitUiState
}

class VisitViewModel(
    pointId: Int,
    private val reading: Int,
    routeRepository: RouteRepository,
    private val visitRepository: VisitRepository,
) : ViewModel() {
    private val saving = MutableStateFlow(false)
    private val result = MutableStateFlow<VisitUiState?>(null)

    val uiState: StateFlow<VisitUiState> = combine(
        routeRepository.observePoint(pointId),
        saving,
        result,
    ) { point, isSaving, completedState ->
        completedState ?: if (point == null) {
            VisitUiState.Error("O ponto desta visita não está mais disponível.")
        } else {
            VisitUiState.Ready(point, reading, isSaving)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VisitUiState.Loading)

    fun saveVisit() {
        val state = uiState.value as? VisitUiState.Ready ?: return
        if (state.saving) return

        viewModelScope.launch {
            saving.value = true
            result.value = runCatching {
                visitRepository.saveVisit(state.point, reading)
                VisitUiState.Saved(state.point, reading)
            }.getOrElse {
                VisitUiState.Error("Não foi possível salvar a visita no dispositivo.")
            }
            saving.value = false
        }
    }
}
