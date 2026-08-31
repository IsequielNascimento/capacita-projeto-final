package com.example.capacita_projeto_final.features.route.domain

data class ProjectedRoutePoint(
    val point: RoutePoint,
    val xFraction: Float,
    val yFraction: Float,
)

fun projectRoutePoints(
    points: List<RoutePoint>,
    insetFraction: Float = 0.1f,
): List<ProjectedRoutePoint> {
    if (points.isEmpty()) return emptyList()

    val inset = insetFraction.coerceIn(0f, 0.45f)
    val latitudeRange = points.maxOf(RoutePoint::latitude) - points.minOf(RoutePoint::latitude)
    val longitudeRange = points.maxOf(RoutePoint::longitude) - points.minOf(RoutePoint::longitude)
    val minimumLatitude = points.minOf(RoutePoint::latitude)
    val minimumLongitude = points.minOf(RoutePoint::longitude)
    val availableFraction = 1f - inset * 2f

    return points.sortedBy(RoutePoint::order).map { point ->
        val x = if (longitudeRange == 0.0) {
            0.5f
        } else {
            inset + ((point.longitude - minimumLongitude) / longitudeRange).toFloat() * availableFraction
        }
        val y = if (latitudeRange == 0.0) {
            0.5f
        } else {
            inset + (1f - ((point.latitude - minimumLatitude) / latitudeRange).toFloat()) * availableFraction
        }
        ProjectedRoutePoint(
            point = point,
            xFraction = x.coerceIn(inset, 1f - inset),
            yFraction = y.coerceIn(inset, 1f - inset),
        )
    }
}
