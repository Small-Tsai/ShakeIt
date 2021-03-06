package com.tsai.shakeit.data.directionPlaceModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Distance(
    val text: String = "",
    val value: Int = 0
) : Parcelable
