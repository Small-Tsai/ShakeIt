package com.tsai.shakeit.data

sealed class Favorite {
    data class ShopName(val name: String) : Favorite()
    data class ShopImg(val img: List<Shop>) : Favorite()
}