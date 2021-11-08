package com.tsai.shakeit.ui.menu

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.launch


class MenuViewModel(
    val selectedShop: Shop,
    private val repository: ShakeItRepository,
    val otherUserId: String?,
) :
    ViewModel() {

    private val _productList = MutableLiveData<List<Menu>>()
    val productList: LiveData<List<Menu>>
        get() = _productList

    private val _navToDetail = MutableLiveData<Product?>()
    val navToDetail: LiveData<Product?>
        get() = _navToDetail

    private val _navToOrder = MutableLiveData<Boolean?>()
    val navToOrder: LiveData<Boolean?>
        get() = _navToOrder

    private val _popBack = MutableLiveData<Boolean?>()
    val popback: LiveData<Boolean?>
        get() = _popBack

    private val _shop = MutableLiveData<Shop?>().apply {
        value = selectedShop
    }
    val shop: LiveData<Shop?>
        get() = _shop

    private var _orderProduct = MutableLiveData<List<OrderProduct>>()
    val orderProduct: LiveData<List<OrderProduct>>
        get() = _orderProduct

    private val _hasOrder = MutableLiveData<Boolean>()
    val hasOrder: LiveData<Boolean>
        get() = _hasOrder

    fun hasOrder() {
        _hasOrder.value = true
    }

    fun noOrder() {
        _hasOrder.value = false
    }

    fun updateOrderTotalPrice(totalPrice: Int) {
        viewModelScope.launch {
            otherUserId?.let {
                when (val result =
                    repository.updateOrderTotalPrice(totalPrice, selectedShop.shop_Id, it)) {
//                is Result.Success -> Log.d(TAG,"update total price")
                }
            }
        }
    }

    private val myId = selectedShop.shop_Id.substring(0, 10) + UserInfo.userId.substring(0, 10)
    private var otherId = ""

    init {
        getProduct()
        getOrderProduct()
    }

    private fun getOrderProduct() {
        if (otherUserId != UserInfo.userId && otherUserId != "") {
            otherId = selectedShop.shop_Id.substring(0, 10) + otherUserId?.substring(0, 10)
            _orderProduct = repository.getFireBaseOrderProduct(otherId)
        } else {
            _orderProduct = repository.getFireBaseOrderProduct(myId)
        }
    }

    private val _branchProduct = MutableLiveData<List<Product>>()
    val branchProduct: LiveData<List<Product>>
        get() = _branchProduct

    private fun getProduct() {
        viewModelScope.launch {
            when (val result = repository.getProduct(selectedShop.name)) {
                is Result.Success -> {
                    _branchProduct.value = result.data!!
                }
                is Result.Fail -> {
                    Toast.makeText(
                        ShakeItApplication.instance,
                        "商品獲取失敗..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Error -> {
                    Logger.d("getProduct Error")
                }
            }
        }
    }

    fun filterMyList(productList: List<Product>) {

        val mList = mutableListOf<Menu>()
        val titleList = mutableListOf<String>()

        for (i in productList.indices) {
            titleList.add(productList[i].type)
        }

        val newTitleList = titleList.distinct()

        for (i in newTitleList.indices) {
            val list = productList.filter { it.type == newTitleList[i] }
            mList.add(Menu.Title(newTitleList[i]))
            for (x in list.indices) {
                mList.add(Menu.MenuProduct(list[x]))
            }
        }
        _productList.value = mList
    }

    fun doNavToDetail(product: Product) {
        _navToDetail.value = product
        _navToDetail.value = null
    }

    fun doNavToOrder() {
        _navToOrder.value = true
        _navToOrder.value = null
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }

    private val _navToAddItem = MutableLiveData<Boolean?>()
    val navToAddItem: LiveData<Boolean?>
        get() = _navToAddItem

    fun navToAddMenuItem() {
        _navToAddItem.value = true
        _navToAddItem.value = null
    }
}

