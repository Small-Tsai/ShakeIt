package com.tsai.shakeit.data.directionPlaceModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
):Parcelable