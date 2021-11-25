package com.tsai.shakeit.data.directionPlaceModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Duration(
    val text: String,
    val value: Int
) : Parcelable
