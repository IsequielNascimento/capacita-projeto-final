package com.example.capacita_projeto_final.features.sync.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capacita_projeto_final.features.sync.data.SyncRepository
import com.example.capacita_projeto_final.features.visit.data.VisitRepository
import com.example.capacita_projeto_final.features.visit.domain.Visit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SyncUiState(
    val pending: Int = 0,
    val synced: Int = 0,
    val errors: Int = 0,
    val running: Boolean = false,
    val message: String? = null,
)

class SyncViewModel(
    visitRepository: VisitRepository,
    private val syncRepository: SyncRepository,
) : ViewModel() {
    private val running = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SyncUiState> = combine(
        visitRepository.observeVisits(),
        running,
        message,
    ) { visits, isRunning, currentMessage ->
        visits.toUiState(isRunning, currentMessage)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SyncUiState())

    fun synchronize() {
        if (running.value) return

        viewModelScope.launch {
            running.value = true
            message.value = null
            message.value = runCatching { syncRepository.synchronizePendingVisits() }
                .fold(
                    onSuccess = { outcome ->
                        when {
                            outcome.failed > 0 -> "${outcome.synchronized} enviada(s), ${outcome.failed} com falha."
                            outcome.synchronized > 0 -> "${outcome.synchronized} visita(s) sincronizada(s)."
                            else -> "Serviço disponível. Nenhuma visita pendente."
                        }
                    },
                    onFailure = { "Não foi possível acessar a API externa." },
                )
            running.value = false
        }
    }
}

private fun List<Visit>.toUiState(running: Boolean, message: String?) = SyncUiState(
    pending = count { it.syncStatus == "pending" || it.syncStatus == "syncing" },
    synced = count { it.syncStatus == "synced" },
    errors = count { it.syncStatus == "error" },
    running = running,
    message = message,
)
