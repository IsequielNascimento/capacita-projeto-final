package com.example.capacita_projeto_final.features.route.domain

data class RoutePoint(
    val id: Int,
    val order: Int,
    val installationCode: String,
    val customer: String,
    val referencePoint: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val meterNumber: String,
    val previousReading: Int,
)
