package com.tsai.shakeit.data.directionPlaceModel

import com.squareup.moshi.Json

data class Leg(

    val distance: Distance = Distance(),

    val duration: Duration = Duration(),

    @Json(name = "end_address")
    val endAddress: String = "",

    @Json(name = "end_location")
    val endLocation: EndLocation? = null,

    @Json(name = "start_address")
    val startAddress: String = "",

    @Json(name = "start_location")
    val startLocation: StartLocation? = null,

    val steps: List<Step> = listOf(),

    @Json(name = "traffic_speed_entry")
    val trafficSpeedEntry: List<Any> = listOf(),

    @Json(name = "via_waypoint")
    val viaWaypoint: List<Any> = listOf(),
)
