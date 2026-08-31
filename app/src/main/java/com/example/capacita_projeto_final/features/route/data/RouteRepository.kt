package com.example.capacita_projeto_final.features.route.data

import com.example.capacita_projeto_final.features.route.data.local.OfficialRoutePoints
import com.example.capacita_projeto_final.features.route.data.local.RoutePointDao
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RouteRepository(
    private val routePointDao: RoutePointDao,
) {
    fun observeRoutePoints(): Flow<List<RoutePoint>> = routePointDao.observeAll().map { points ->
        points.map { it.toDomain() }
    }

    fun observePoint(pointId: Int): Flow<RoutePoint?> = routePointDao.observeById(pointId).map { point ->
        point?.toDomain()
    }

    suspend fun seedIfEmpty() {
        if (routePointDao.count() == 0) {
            routePointDao.insertAll(OfficialRoutePoints)
        }
    }
}
