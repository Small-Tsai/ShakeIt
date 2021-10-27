package com.tsai.shakeit.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import kotlinx.coroutines.launch

class HomeDialogViewModel(private val repository: ShakeItRepository, val shopData: Shop? ) : ViewModel() {

    private val _hasNavToMenu = MutableLiveData<Shop?>()
    val hasNavToMenu: LiveData<Shop?>
        get() = _hasNavToMenu

    private val _isInserted = MutableLiveData<Boolean>()
    val isInserted: LiveData<Boolean>
        get() = _isInserted

    private var _shop = MutableLiveData<List<Shop>>()
    val shop: LiveData<List<Shop>>
        get() = _shop

    var order = MutableLiveData<List<Order>>()

    init {
        order = repository.getShopOrder(shopData!!.shop_Id)
        Log.d(TAG,"shopData = $shopData")
    }

    var orderId = ""
    fun checkHasOrder(order: List<Order>) {
        val currentShopOrder = order.filter { it.branch == shopData!!.branch }

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
                repository.postFavorite(shopData!!)) {
                is Result.Success -> {
                    Toast.makeText(
                        ShakeItApplication.instance,
                        "已將 ${shopData.name + shopData.branch} 加入收藏",
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
            repository.deleteFavorite(shopData!!.shop_Id)
        }
    }

    fun checkHasFavorite(shop: List<Shop>) {
        _isInserted.value = shop.map { it.shop_Id }.contains(shopData!!.shop_Id)
    }
}