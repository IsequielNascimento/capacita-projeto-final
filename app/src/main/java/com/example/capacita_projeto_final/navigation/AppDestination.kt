package com.example.capacita_projeto_final.navigation

// MARK: - Tabs

enum class AppTab(val graphRoute: String, val startRoute: String) {
    Route("graph/route", "route/list"),
    Map("graph/map", "map/canvas"),
    Sync("graph/sync", "sync/list"),
}

// MARK: - Routes

object AppRoute {
    const val RoutePointDetail = "route/point/{pointId}"
    const val MapPointDetail = "map/point/{pointId}"
    const val VisitSheet = "visit/{pointId}/{reading}"

    fun routePointDetail(pointId: Int) = "route/point/$pointId"
    fun mapPointDetail(pointId: Int) = "map/point/$pointId"
    fun visitSheet(pointId: Int, reading: Int) = "visit/$pointId/$reading"
}
