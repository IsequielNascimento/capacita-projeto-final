package com.example.capacita_projeto_final.features.visit.domain

data class Visit(
    val id: String,
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
)
