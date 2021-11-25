package com.tsai.shakeit.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val user_Id: String = "",
    val user_Name: String = "",
    val user_Image: String = "",
    var user_Token: String = ""
) : Parcelable
