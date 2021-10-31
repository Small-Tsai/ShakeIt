package com.tsai.shakeit.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ui.addshop.AddShopViewModel
import com.tsai.shakeit.ui.home.comment.CommentViewModel
import com.tsai.shakeit.ui.menu.detail.DrinksDetailViewModel
import com.tsai.shakeit.ui.favorite.FavoriteViewModel
import com.tsai.shakeit.ui.home.HomeViewModel
import com.tsai.shakeit.ui.menu.MenuViewModel
import com.tsai.shakeit.ui.order.OrderViewModel
import com.tsai.shakeit.ui.order.sendcomment.CommentDialogViewModel
import com.tsai.shakeit.ui.orderdetail.OrderDetailViewModel
import com.tsai.shakeit.ui.setting.SettingViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val product: Product? = null,
    private val order: Order? = null,
    private val shop: Shop? = null,
    private val repository: ShakeItRepository,
    private val shopId: String? = "",
    private val shopList: Array<Shop> = arrayOf(),
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
                    shop?.let { MenuViewModel(it, repository) }

                isAssignableFrom(CommentViewModel::class.java) ->
                    CommentViewModel(repository , shopId)

                isAssignableFrom(CommentDialogViewModel::class.java) ->
                    CommentDialogViewModel(repository, shopId)

                isAssignableFrom(AddShopViewModel::class.java) ->
                    AddShopViewModel(repository)

                isAssignableFrom(SettingViewModel::class.java) ->
                    SettingViewModel(repository, shopList)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
