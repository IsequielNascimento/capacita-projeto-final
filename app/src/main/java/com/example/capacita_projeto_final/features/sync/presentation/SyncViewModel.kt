package com.example.capacita_projeto_final.features.sync.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capacita_projeto_final.features.sync.data.SyncRepository
import com.example.capacita_projeto_final.features.visit.data.VisitRepository
import com.example.capacita_projeto_final.features.visit.domain.SyncStatus
import com.example.capacita_projeto_final.features.visit.domain.Visit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// MARK: - State

sealed interface SyncFeedback {
    data class Completed(val synchronized: Int) : SyncFeedback
    data class PartiallyFailed(val synchronized: Int, val failed: Int) : SyncFeedback
    data object NothingPending : SyncFeedback
    data object ServiceUnavailable : SyncFeedback
}

data class SyncUiState(
    val pending: Int = 0,
    val synced: Int = 0,
    val errors: Int = 0,
    val running: Boolean = false,
    val feedback: SyncFeedback? = null,
    val visits: List<Visit> = emptyList(),
) {
    val failed: Boolean
        get() = feedback is SyncFeedback.ServiceUnavailable || feedback is SyncFeedback.PartiallyFailed
}

// MARK: - View model

class SyncViewModel(
    visitRepository: VisitRepository,
    private val syncRepository: SyncRepository,
) : ViewModel() {
    private val running = MutableStateFlow(false)
    private val feedback = MutableStateFlow<SyncFeedback?>(null)

    val uiState: StateFlow<SyncUiState> = combine(
        visitRepository.observeVisits(),
        running,
        feedback,
    ) { visits, isRunning, currentFeedback ->
        visits.toUiState(isRunning, currentFeedback)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SyncUiState())

    fun synchronize() {
        if (running.value) return

        viewModelScope.launch {
            running.value = true
            feedback.value = null
            feedback.value = runCatching { syncRepository.synchronizePendingVisits() }
                .fold(
                    onSuccess = { outcome ->
                        when {
                            outcome.failed > 0 -> SyncFeedback.PartiallyFailed(outcome.synchronized, outcome.failed)
                            outcome.synchronized > 0 -> SyncFeedback.Completed(outcome.synchronized)
                            else -> SyncFeedback.NothingPending
                        }
                    },
                    onFailure = { SyncFeedback.ServiceUnavailable },
                )
            running.value = false
        }
    }
}

private fun List<Visit>.toUiState(running: Boolean, feedback: SyncFeedback?) = SyncUiState(
    pending = count { it.syncStatus == SyncStatus.Pending || it.syncStatus == SyncStatus.Sending },
    synced = count { it.syncStatus == SyncStatus.Sent },
    errors = count { it.syncStatus == SyncStatus.Failed },
    running = running,
    feedback = feedback,
    visits = this,
)
