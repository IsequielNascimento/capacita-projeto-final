package com.example.capacita_projeto_final.features.visit.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.capacita_projeto_final.features.route.data.local.RoutePointEntity
import com.example.capacita_projeto_final.features.visit.domain.SyncStatus
import com.example.capacita_projeto_final.features.visit.domain.Visit

@Entity(
    tableName = "visits",
    foreignKeys = [
        ForeignKey(
            entity = RoutePointEntity::class,
            parentColumns = ["id"],
            childColumns = ["pointId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("pointId")],
)
data class VisitEntity(
    @PrimaryKey val id: String,
    val pointId: Int,
    val installationCode: String,
    val meterNumber: String,
    val previousReading: Int,
    val currentReading: Int,
    val photoUri: String?,
    val latitude: Double?,
    val longitude: Double?,
    val capturedAt: Long,
    val syncStatus: String,
) {
    fun toDomain() = Visit(
        id = id,
        pointId = pointId,
        installationCode = installationCode,
        meterNumber = meterNumber,
        previousReading = previousReading,
        currentReading = currentReading,
        photoUri = photoUri,
        latitude = latitude,
        longitude = longitude,
        capturedAt = capturedAt,
        syncStatus = SyncStatus.fromStorage(syncStatus),
    )
}
