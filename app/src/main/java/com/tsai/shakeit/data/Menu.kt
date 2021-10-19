package com.tsai.shakeit.data

sealed class Menu{
    data class Title (val type: String):Menu()
    data class MenuProduct (val product: Product):Menu()
}