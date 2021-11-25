package com.tsai.shakeit.data.directionPlaceModel

import com.squareup.moshi.Json

data class Route(

    val bounds: Bounds,

    val copyrights: String,

    val legs: List<Leg>,

    @Json(name = "overview_polyline")
    val overview_Polyline: OverviewPolyline,

    val summary: String,

    val warnings: List<String>,

    @Json(name = "waypoint_order")
    val waypoint_Order: List<Any>
)
