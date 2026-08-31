package com.example.capacita_projeto_final.features.visit.data

import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.visit.data.local.VisitDao
import com.example.capacita_projeto_final.features.visit.data.local.VisitEntity
import com.example.capacita_projeto_final.features.visit.domain.SyncStatus
import com.example.capacita_projeto_final.features.visit.domain.Visit
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VisitRepository(
    private val visitDao: VisitDao,
) {
    fun observeVisits(): Flow<List<Visit>> = visitDao.observeAll().map { visits ->
        visits.map { it.toDomain() }
    }

    fun observeLatestForPoint(pointId: Int): Flow<Visit?> =
        visitDao.observeLatestForPoint(pointId).map { it?.toDomain() }

    suspend fun saveVisit(
        point: RoutePoint,
        currentReading: Int,
        photoUri: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
    ): Visit {
        val visit = VisitEntity(
            id = UUID.randomUUID().toString(),
            pointId = point.id,
            installationCode = point.installationCode,
            meterNumber = point.meterNumber,
            previousReading = point.previousReading,
            currentReading = currentReading,
            photoUri = photoUri,
            latitude = latitude,
            longitude = longitude,
            capturedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.Pending.storageValue,
        )
        visitDao.insert(visit)
        return visit.toDomain()
    }

    suspend fun pendingVisits(): List<Visit> = visitDao.getPending().map { it.toDomain() }

    suspend fun updateSyncStatus(visitId: String, status: SyncStatus) {
        visitDao.updateSyncStatus(visitId, status.storageValue)
    }
}
