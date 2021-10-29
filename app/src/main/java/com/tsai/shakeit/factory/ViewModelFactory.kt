package com.tsai.shakeit.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ui.detail.DrinksDetailViewModel
import com.tsai.shakeit.ui.favorite.FavoriteViewModel
import com.tsai.shakeit.ui.home.HomeViewModel
import com.tsai.shakeit.ui.menu.MenuViewModel
import com.tsai.shakeit.ui.order.OrderViewModel
import com.tsai.shakeit.ui.orderdetail.OrderDetailViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val product: Product? = null,
    private val order: Order? = null,
    private val shop:Shop? = null,
    private val repository: ShakeItRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository)

                isAssignableFrom(DrinksDetailViewModel::class.java) ->
                    product?.let { DrinksDetailViewModel(it, repository) }

                isAssignableFrom(OrderDetailViewModel::class.java) ->
                    OrderDetailViewModel(order, repository)

                isAssignableFrom(OrderViewModel::class.java) ->
                    OrderViewModel(repository)

                isAssignableFrom(FavoriteViewModel::class.java) ->
                    FavoriteViewModel(repository)

                isAssignableFrom(MenuViewModel::class.java) ->
                    shop?.let { MenuViewModel(it,repository) }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}