package com.tsai.shakeit.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Shop(
    val name: String = "",
    var shop_Id: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val tel: Int = 0,
    val time: Timestamp? = null,
    val address: String = "",
    val avg_Score: Double = 0.0,
    val shop_Img: String = "",
    val branch: String = ""
) : Parcelable {
}