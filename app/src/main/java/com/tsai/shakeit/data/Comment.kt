package com.tsai.shakeit.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val comment: String = "",
    val rating: Float = 0f,
    val user: User? = null,
    val date: Timestamp? = null
) : Parcelable

