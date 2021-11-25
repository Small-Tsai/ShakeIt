package com.tsai.shakeit.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Favorite(
    val shop: Shop = Shop(),
    val user_Id: String = ""
) : Parcelable
