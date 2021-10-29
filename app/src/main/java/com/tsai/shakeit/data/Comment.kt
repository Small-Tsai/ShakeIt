package com.tsai.shakeit.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(

    val comment: String = "",
    val rating: Float = 0f,
    val user: User? = null

) : Parcelable

