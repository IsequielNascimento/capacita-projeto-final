package com.example.capacita_projeto_final.features.route.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteMapProjectionTest {
    @Test
    fun `projects every point inside the visible map area in route order`() {
        val points = listOf(
            routePoint(id = 3, order = 3, latitude = -3.7390, longitude = -38.5077),
            routePoint(id = 1, order = 1, latitude = -3.7288, longitude = -38.5164),
            routePoint(id = 2, order = 2, latitude = -3.7332, longitude = -38.5091),
        )

        val projected = projectRoutePoints(points)

        assertEquals(listOf(1, 2, 3), projected.map { it.point.order })
        assertTrue(projected.all { it.xFraction in 0.1f..0.9f })
        assertTrue(projected.all { it.yFraction in 0.1f..0.9f })
        assertTrue(projected.map { it.xFraction to it.yFraction }.distinct().size > 1)
    }

    @Test
    fun `centers a route with a single point`() {
        val projected = projectRoutePoints(
            listOf(routePoint(id = 1, order = 1, latitude = -3.73, longitude = -38.51)),
        )

        assertEquals(0.5f, projected.single().xFraction)
        assertEquals(0.5f, projected.single().yFraction)
    }

    private fun routePoint(
        id: Int,
        order: Int,
        latitude: Double,
        longitude: Double,
    ) = RoutePoint(
        id = id,
        order = order,
        installationCode = "CAP-ALD-$id",
        customer = "Residência $id",
        referencePoint = "Referência $id",
        address = "Endereço $id",
        latitude = latitude,
        longitude = longitude,
        meterNumber = "MED-$id",
        previousReading = 100,
    )
}
