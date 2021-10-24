package com.tsai.shakeit.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ui.detail.DrinksDetailViewModel
import com.tsai.shakeit.ui.favorite.FavoriteViewModel
import com.tsai.shakeit.ui.home.HomeViewModel
import com.tsai.shakeit.ui.order.OrderViewModel
import com.tsai.shakeit.ui.orderdetail.OrderDetailViewModel

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Factory for all ViewModels which need [Product].
 */
@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val product: Product? = null,
    private val order: Order? = null,
    private val repository: ShakeItRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
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

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
