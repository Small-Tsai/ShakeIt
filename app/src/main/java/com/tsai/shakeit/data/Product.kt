package com.tsai.shakeit.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val name: String = "",
    val content: String = "",
    val capacity: HashMap<String, Int> = hashMapOf(),
    val sugar: HashMap<String, Int> = hashMapOf(),
    val ice: HashMap<String, Int> = hashMapOf(),
    var price: Int = 0,
    val others: HashMap<String, Int> = hashMapOf(),
    val shopId: String = "",
    val type: String = "",
    var id: String = "",
    val shop_Name: String = "",
    val shopAddress: String = "",
    val branch: String = "",
    val product_Img: String = "",
) : Parcelable



