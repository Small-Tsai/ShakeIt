package com.tsai.shakeit.data.directionPlaceModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bounds(
    val northeast: Northeast,
    val southwest: Southwest
) : Parcelable
