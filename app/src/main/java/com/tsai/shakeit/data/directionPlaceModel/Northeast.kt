package com.tsai.shakeit.data.directionPlaceModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Northeast(
    val lat: Double,
    val lng: Double
) : Parcelable
