package com.tsai.shakeit.data.directionPlaceModel

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Step(

    val distance: Distance,

    val duration: Duration,

    @Json(name = "end_location")
    val end_location: EndLocation,

    @Json(name = "html_instructions")
    val htmlInstructions: String,

    val maneuver: String? = null,

    val polyline: Polyline,

    @Json(name = "start_location")
    val startLocation: StartLocation,

    @Json(name = "travel_mode")
    val travelMode: String

) : Parcelable
