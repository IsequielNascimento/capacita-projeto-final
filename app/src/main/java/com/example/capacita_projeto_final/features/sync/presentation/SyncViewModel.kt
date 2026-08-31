package com.example.capacita_projeto_final.features.sync.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capacita_projeto_final.core.notification.VisitNotifier
import com.example.capacita_projeto_final.core.notification.syncResultNotification
import com.example.capacita_projeto_final.features.sync.data.SyncOutcome
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
    private val visitRepository: VisitRepository,
    private val syncRepository: SyncRepository,
    private val visitNotifier: VisitNotifier,
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

            val outcome = runCatching {
                syncRepository.synchronizePendingVisits { completed, total ->
                    progress.value = SyncProgress(completed, total)
                }
            }
            feedback.value = outcome.fold(
                onSuccess = SyncOutcome::toFeedback,
                onFailure = { SyncFeedback.ServiceUnavailable },
            )
            announceResult(synchronized = outcome.getOrNull()?.synchronized ?: 0)

            running.value = false
            progress.value = null
        }
    }

    /**
     * Avisa o resultado do envio feito pela tela, usando a mesma decisão do envio
     * disparado pela notificação. O aviso é secundário: falhar aqui não desfaz o envio.
     */
    private suspend fun announceResult(synchronized: Int) {
        runCatching {
            val remaining = visitRepository.pendingVisits().size
            when (val result = syncResultNotification(synchronized, remaining)) {
                null -> visitNotifier.dismiss()
                else -> visitNotifier.show(result)
            }
        }
    }
}

fun SyncOutcome.toFeedback(): SyncFeedback = when {
    failed > 0 -> SyncFeedback.PartiallyFailed(synchronized, failed)
    synchronized > 0 -> SyncFeedback.Completed(synchronized)
    else -> SyncFeedback.NothingPending
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
