package com.example.capacita_projeto_final.features.visit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {
    @Query("SELECT * FROM visits ORDER BY capturedAt DESC")
    fun observeAll(): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE pointId = :pointId ORDER BY capturedAt DESC LIMIT 1")
    fun observeLatestForPoint(pointId: Int): Flow<VisitEntity?>

    @Query("SELECT * FROM visits WHERE syncStatus IN ('pending', 'error') ORDER BY capturedAt")
    suspend fun getPending(): List<VisitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(visit: VisitEntity)

    @Query("UPDATE visits SET syncStatus = :status WHERE id = :visitId")
    suspend fun updateSyncStatus(visitId: String, status: String)
}
