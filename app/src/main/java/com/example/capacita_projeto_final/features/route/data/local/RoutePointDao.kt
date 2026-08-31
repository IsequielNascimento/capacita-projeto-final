package com.example.capacita_projeto_final.features.route.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutePointDao {
    @Query("SELECT * FROM route_points ORDER BY visitOrder")
    fun observeAll(): Flow<List<RoutePointEntity>>

    @Query("SELECT * FROM route_points WHERE id = :pointId")
    fun observeById(pointId: Int): Flow<RoutePointEntity?>

    @Query("SELECT COUNT(*) FROM route_points")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<RoutePointEntity>)
}
