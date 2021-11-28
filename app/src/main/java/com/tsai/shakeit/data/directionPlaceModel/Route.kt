package com.tsai.shakeit.data.directionPlaceModel

import com.squareup.moshi.Json

data class Route(

    val bounds: Bounds? = null,

    val copyrights: String = "",

    val legs: List<Leg> = listOf(),

    @Json(name = "overview_polyline")
    val overview_Polyline: OverviewPolyline? = null,

    val summary: String = "",

    val warnings: List<String> = listOf(),

    @Json(name = "waypoint_order")
    val waypoint_Order: List<Any> = listOf(),
)
