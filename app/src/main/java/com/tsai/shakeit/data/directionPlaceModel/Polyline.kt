package com.tsai.shakeit.data.directionPlaceModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Polyline(
    val points: String
):Parcelable