package com.tsai.shakeit.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User(
    val user_Id: String = "",
    val user_Name: String = "",
    val user_Image: String = ""
) : Parcelable {
}