package com.example.capacita_projeto_final.features.point.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capacita_projeto_final.features.route.data.RouteRepository
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.visit.data.VisitRepository
import com.example.capacita_projeto_final.features.visit.domain.ReadingError
import com.example.capacita_projeto_final.features.visit.domain.ReadingValidation
import com.example.capacita_projeto_final.features.visit.domain.Visit
import com.example.capacita_projeto_final.features.visit.domain.validateReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface PointDetailUiState {
    data object Loading : PointDetailUiState
    data class Ready(
        val point: RoutePoint,
        val latestVisit: Visit?,
        val readingInput: String,
        val validationError: ReadingError?,
    ) : PointDetailUiState
    data object NotFound : PointDetailUiState
}

class PointDetailViewModel(
    pointId: Int,
    routeRepository: RouteRepository,
    visitRepository: VisitRepository,
) : ViewModel() {
    private val readingInput = MutableStateFlow("")
    private val validationError = MutableStateFlow<ReadingError?>(null)

    val uiState: StateFlow<PointDetailUiState> = combine(
        routeRepository.observePoint(pointId),
        visitRepository.observeLatestForPoint(pointId),
        readingInput,
        validationError,
    ) { point, latestVisit, input, error ->
        if (point == null) {
            PointDetailUiState.NotFound
        } else {
            PointDetailUiState.Ready(point, latestVisit, input, error)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PointDetailUiState.Loading)

    fun updateReading(value: String) {
        readingInput.value = value.filter(Char::isDigit)
        validationError.value = null
    }

    fun prepareVisit(): Int? {
        val state = uiState.value as? PointDetailUiState.Ready ?: return null
        return when (val result = validateReading(state.readingInput, state.point.previousReading)) {
            is ReadingValidation.Valid -> result.reading
            is ReadingValidation.Invalid -> {
                validationError.value = result.reason
                null
            }
        }
    }
}
