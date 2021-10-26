package com.tsai.shakeit.ui.home

import android.util.Log
import android.widget.Toast
import androidx.core.graphics.scaleMatrix
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import kotlinx.coroutines.launch

class HomeDialogViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private val _hasNavToMenu = MutableLiveData<Shop?>()
    val hasNavToMenu: LiveData<Shop?>
        get() = _hasNavToMenu

    private val _isInserted = MutableLiveData<Boolean>()
    val isInserted: LiveData<Boolean>
        get() = _isInserted

    private var _shop = MutableLiveData<List<Shop>>()
    val shop: LiveData<List<Shop>>
        get() = _shop

    var _order = MutableLiveData<List<Order>>()


    val name = "茶湯會"
    val branch = "公館商圈店"
    val shop_Id = "oA5Ze5yYAG7Dp5QcVB7B"
    val shop_Img = "images/teasoup"

    val selectedShop = Shop(
        name = name,
        branch = branch,
        shop_Id = shop_Id,
        shop_Img = shop_Img,
    )

    init {
//        getMyFavorite()
        _order = repository.getFireBaseOrder()
    }

    var orderId = ""
    fun checkHasOrder(order: List<Order>) {
        val currentShopOrder = order.filter { it.branch == branch }

        if (currentShopOrder.isNotEmpty()) {
            orderId = currentShopOrder.first().order_Id
        }
    }

    fun navToMenu(shop: Shop) {
        _hasNavToMenu.value = shop
        _hasNavToMenu.value = null
    }

    fun postMyFavorite() {
        viewModelScope.launch {
            when (val result =
                repository.postFavorite(
                    Shop(
                        name = name,
                        branch = branch,
                        shop_Id = shop_Id,
                        shop_Img = shop_Img
                    )
                )) {
                is Result.Success -> {
                    Toast.makeText(
                        ShakeItApplication.instance,
                        "已將 ${name + branch} 加入收藏",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Fail -> {
                    Toast.makeText(
                        ShakeItApplication.instance,
                        "加入收藏失敗",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Error -> {
                    Log.d(TAG, "postFavorite Error")
                }
            }
        }
    }


    fun getMyFavorite() {
        viewModelScope.launch {
            _shop = repository.getFavorite()
        }
    }

    fun deleteFavorite() {
        viewModelScope.launch {
            repository.deleteFavorite(shop_Id)
        }
    }

    fun checkHasFavorite(shop: List<Shop>) {
        _isInserted.value = shop.map { it.shop_Id }.contains(shop_Id)
    }
}