package com.example.capacita_projeto_final.features.route.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.capacita_projeto_final.features.route.domain.RoutePoint

@Entity(tableName = "route_points")
data class RoutePointEntity(
    @PrimaryKey val id: Int,
    val visitOrder: Int,
    val installationCode: String,
    val customer: String,
    val referencePoint: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val meterNumber: String,
    val previousReading: Int,
) {
    fun toDomain() = RoutePoint(
        id = id,
        order = visitOrder,
        installationCode = installationCode,
        customer = customer,
        referencePoint = referencePoint,
        address = address,
        latitude = latitude,
        longitude = longitude,
        meterNumber = meterNumber,
        previousReading = previousReading,
    )
}
