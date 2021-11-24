package com.tsai.shakeit.data.directionPlaceModel


data class Direction(
    val error: String? = null,
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)