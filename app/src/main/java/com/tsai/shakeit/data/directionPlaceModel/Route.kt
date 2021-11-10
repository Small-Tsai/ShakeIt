package com.tsai.shakeit.data.directionPlaceModel

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize


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