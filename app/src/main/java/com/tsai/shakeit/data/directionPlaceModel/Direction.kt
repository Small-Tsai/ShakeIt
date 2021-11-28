package com.tsai.shakeit.data.directionPlaceModel

data class Direction(
    val error: String? = null,
    val geocoded_waypoints: List<GeocodedWaypoint> = listOf(),
    val routes: List<Route> = listOf(),
    val status: String = "",
)
