package com.example.capacita_projeto_final.features.visit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capacita_projeto_final.features.route.data.RouteRepository
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.visit.data.VisitRepository
import com.example.capacita_projeto_final.features.visit.infrastructure.DeviceLocation
import com.example.capacita_projeto_final.features.visit.infrastructure.DeviceLocationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface VisitUiState {
    data object Loading : VisitUiState
    data class Ready(
        val point: RoutePoint,
        val reading: Int,
        val saving: Boolean,
        val photoUri: String?,
        val location: DeviceLocation?,
        val locationLoading: Boolean,
        val evidenceMessage: String?,
    ) : VisitUiState
    data class Saved(val point: RoutePoint, val reading: Int) : VisitUiState
    data class Error(val message: String) : VisitUiState
}

class VisitViewModel(
    pointId: Int,
    private val reading: Int,
    routeRepository: RouteRepository,
    private val visitRepository: VisitRepository,
    private val locationProvider: DeviceLocationProvider,
) : ViewModel() {
    private val saving = MutableStateFlow(false)
    private val result = MutableStateFlow<VisitUiState?>(null)
    private val evidence = MutableStateFlow(VisitEvidenceState())

    val uiState: StateFlow<VisitUiState> = combine(
        routeRepository.observePoint(pointId),
        saving,
        result,
        evidence,
    ) { point, isSaving, completedState, currentEvidence ->
        completedState ?: if (point == null) {
            VisitUiState.Error("O ponto desta visita não está mais disponível.")
        } else {
            VisitUiState.Ready(
                point = point,
                reading = reading,
                saving = isSaving,
                photoUri = currentEvidence.photoUri,
                location = currentEvidence.location,
                locationLoading = currentEvidence.locationLoading,
                evidenceMessage = currentEvidence.message,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VisitUiState.Loading)

    fun saveVisit() {
        val state = uiState.value as? VisitUiState.Ready ?: return
        if (state.saving) return

        viewModelScope.launch {
            saving.value = true
            result.value = runCatching {
                visitRepository.saveVisit(
                    point = state.point,
                    currentReading = reading,
                    photoUri = state.photoUri,
                    latitude = state.location?.latitude,
                    longitude = state.location?.longitude,
                )
                VisitUiState.Saved(state.point, reading)
            }.getOrElse {
                VisitUiState.Error("Não foi possível salvar a visita no dispositivo.")
            }
            saving.value = false
        }
    }

    fun confirmPhoto(uri: String?) {
        evidence.value = if (uri == null) {
            evidence.value.copy(message = "A foto não foi capturada.")
        } else {
            evidence.value.copy(photoUri = uri, message = "Foto anexada à visita.")
        }
    }

    fun reportEvidenceMessage(message: String) {
        evidence.value = evidence.value.copy(message = message)
    }

    fun captureLocation() {
        if (evidence.value.locationLoading) return

        viewModelScope.launch {
            evidence.value = evidence.value.copy(locationLoading = true, message = null)
            evidence.value = runCatching { locationProvider.currentLocation() }
                .fold(
                    onSuccess = { location ->
                        evidence.value.copy(
                            location = location,
                            locationLoading = false,
                            message = "Localização anexada à visita.",
                        )
                    },
                    onFailure = {
                        evidence.value.copy(
                            locationLoading = false,
                            message = "Não foi possível obter a localização atual.",
                        )
                    },
                )
        }
    }
}

private data class VisitEvidenceState(
    val photoUri: String? = null,
    val location: DeviceLocation? = null,
    val locationLoading: Boolean = false,
    val message: String? = null,
)
