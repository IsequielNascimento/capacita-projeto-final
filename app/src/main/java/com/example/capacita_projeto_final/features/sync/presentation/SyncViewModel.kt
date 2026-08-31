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

data class SyncProgress(val completed: Int, val total: Int) {
    val fraction: Float
        get() = if (total <= 0) 0f else completed.toFloat() / total.toFloat()
}

data class SyncUiState(
    val pending: Int = 0,
    val synced: Int = 0,
    val errors: Int = 0,
    val running: Boolean = false,
    val feedback: SyncFeedback? = null,
    val visits: List<Visit> = emptyList(),
    val progress: SyncProgress? = null,
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
    private val progress = MutableStateFlow<SyncProgress?>(null)

    val uiState: StateFlow<SyncUiState> = combine(
        visitRepository.observeVisits(),
        running,
        feedback,
        progress,
    ) { visits, isRunning, currentFeedback, currentProgress ->
        visits.toUiState(isRunning, currentFeedback, currentProgress)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SyncUiState())

    fun synchronize() {
        if (running.value) return

        viewModelScope.launch {
            running.value = true
            feedback.value = null
            progress.value = null
            feedback.value = runCatching {
                syncRepository.synchronizePendingVisits { completed, total ->
                    progress.value = SyncProgress(completed, total)
                }
            }
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
            progress.value = null
        }
    }
}

private fun List<Visit>.toUiState(
    running: Boolean,
    feedback: SyncFeedback?,
    progress: SyncProgress?,
) = SyncUiState(
    pending = count { it.syncStatus == SyncStatus.Pending || it.syncStatus == SyncStatus.Sending },
    synced = count { it.syncStatus == SyncStatus.Sent },
    errors = count { it.syncStatus == SyncStatus.Failed },
    running = running,
    feedback = feedback,
    visits = this,
    progress = progress,
)
