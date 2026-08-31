package com.example.capacita_projeto_final.features.sync.data

import com.example.capacita_projeto_final.features.sync.data.remote.CapacitaApi
import com.example.capacita_projeto_final.features.sync.data.remote.VisitPayload
import com.example.capacita_projeto_final.features.visit.data.VisitRepository
import com.example.capacita_projeto_final.features.visit.domain.SyncStatus
import com.example.capacita_projeto_final.features.visit.domain.Visit

data class SyncOutcome(
    val serviceName: String,
    val synchronized: Int,
    val failed: Int,
)

class SyncRepository(
    private val api: CapacitaApi,
    private val visitRepository: VisitRepository,
) {
    suspend fun synchronizePendingVisits(): SyncOutcome {
        val serviceStatus = api.getServiceStatus()
        val pendingVisits = visitRepository.pendingVisits()
        var synchronized = 0
        var failed = 0

        pendingVisits.forEach { visit ->
            visitRepository.updateSyncStatus(visit.id, SyncStatus.Sending)
            runCatching { api.sendVisit(visit.toPayload()) }
                .onSuccess {
                    visitRepository.updateSyncStatus(visit.id, SyncStatus.Sent)
                    synchronized += 1
                }
                .onFailure {
                    visitRepository.updateSyncStatus(visit.id, SyncStatus.Failed)
                    failed += 1
                }
        }

        return SyncOutcome(
            serviceName = serviceStatus.title,
            synchronized = synchronized,
            failed = failed,
        )
    }
}

fun Visit.toPayload() = VisitPayload(
    userId = pointId,
    title = "Visita $installationCode",
    body = listOf(
        "medidor=$meterNumber",
        "leituraAnterior=$previousReading",
        "leituraAtual=$currentReading",
        "capturadaEm=$capturedAt",
        "latitude=${latitude ?: ""}",
        "longitude=${longitude ?: ""}",
    ).joinToString(";"),
)
