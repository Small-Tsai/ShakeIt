package com.tsai.shakeit.ui.menu

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tsai.shakeit.R
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import com.tsai.shakeit.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuViewModel(
    val selectedShop: Shop,
    private val repository: ShakeItRepository,
    val otherUserId: String?,
) : ViewModel() {

    private val _menuList = MutableLiveData<List<Menu>>()
    val menuList: LiveData<List<Menu>>
        get() = _menuList

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

    private var _orderProductList = MutableLiveData<List<OrderProduct>>()
    val orderProductList: LiveData<List<OrderProduct>>
        get() = _orderProductList

    private var _orderList = MutableLiveData<List<Order>>()
    val orderList: LiveData<List<Order>>
        get() = _orderList

    private val _hasOrderProduct = MutableLiveData<Boolean>()
    val hasOrderProduct: LiveData<Boolean>
        get() = _hasOrderProduct

    private val _hasOrder = MutableLiveData<Boolean>()
    val hasOrder: LiveData<Boolean>
        get() = _hasOrder

    private val _showDialog = MutableLiveData<Boolean?>()
    val showDialog: LiveData<Boolean?>
        get() = _showDialog

    private val _shareOrder = MutableLiveData<Intent?>()
    val shareOrder: LiveData<Intent?>
        get() = _shareOrder

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    private val _navToAddItem = MutableLiveData<Boolean?>()
    val navToAddItem: LiveData<Boolean?>
        get() = _navToAddItem

    var orderName = MutableLiveData<String>().apply {
        value = "我的訂單"
    }

    private val myId = selectedShop.shop_Id.substring(0, 10) + UserInfo.userId.substring(0, 10)

    private val otherId: String
        get() {
            return selectedShop.shop_Id.substring(0, 10) + otherUserId?.substring(0, 10)
        }

    private val mOrder = Order(
        shop_Name = selectedShop.name,
        branch = selectedShop.branch,
        date = Timestamp.now(),
        order_Name = orderName.value!!,
        shop_Id = selectedShop.shop_Id,
        user_Id = UserInfo.userId,
        invitation = arrayListOf(UserInfo.userId),
        shop_Img = selectedShop.shop_Img
    )

    private fun shareOrderToLINE() {
        mOrder.order_Id = myId
        val lineUrl = "https://line.me/R/msg/text/快來跟我一起喝${selectedShop.name}吧！" +
            "https://com.smalltsai.shakeit/${mOrder.order_Id}"
        val sendIntent = Intent.parseUri(lineUrl, Intent.URI_INTENT_SCHEME)
        _shareOrder.value = sendIntent
        _shareOrder.value = null
        _status.value = LoadApiStatus.DONE
    }

    fun startShare() {
        if (_hasOrder.value == false) {
            _showDialog.value = true
            _showDialog.value = null
        } else {
            _status.value = LoadApiStatus.LOADING
            shareOrderToLINE()
        }
    }

    fun addNewDocToFireBase() {
        if (!Util.isInternetConnected()) {
            _status.value = LoadApiStatus.ERROR
            myToast(Util.getString(R.string.internet_not_connected))
        } else {
            viewModelScope.launch {
                _status.value = LoadApiStatus.LOADING
                mOrder.order_Name = orderName.value.toString()
                when (
                    val result =
                        withContext(Dispatchers.IO) { repository.crateNewOrderForShare(mOrder) }
                ) {
                    is Result.Success -> {
                        shareOrderToLINE()
                        _showDialog.value = false
                    }
                    is Result.Fail -> {
                        Logger.e(result.error)
                    }
                }
            }
        }
    }

    fun hasOrderProduct() {
        _hasOrderProduct.value = true
    }

    fun noOrderProduct() {
        _hasOrderProduct.value = false
    }

    fun hasOrder() {
        _hasOrder.value = true
    }

    fun noOrder() {
        _hasOrder.value = false
    }

    fun updateOrderTotalPrice(totalPrice: Int) {
        viewModelScope.launch {
            otherUserId?.let {
                when (
                    val result =
                        withContext(Dispatchers.IO) {
                            repository.updateOrderTotalPrice(totalPrice, selectedShop.shop_Id, it)
                        }
                ) {
                    is Result.Success -> Logger.d("update Price")
                    is Result.Fail -> Logger.e(result.error)
                }
            }
        }
    }

    fun initProduct() {
        getProduct()
        getOrder()
        getOrderProduct()
    }

    private fun getOrder() {
        _orderList = if (otherUserId != UserInfo.userId && otherUserId != "") {
            repository.getOrderByOrderId(otherId)
        } else {
            repository.getOrderByOrderId(myId)
        }
    }

    private fun getOrderProduct() {
        _orderProductList = if (otherUserId != UserInfo.userId && otherUserId != "") {
            repository.getOrderProductBySnapShot(otherId)
        } else {
            repository.getOrderProductBySnapShot(myId)
        }
    }

    private val _branchProductList = MutableLiveData<List<Product>>()
    val branchProductList: LiveData<List<Product>>
        get() = _branchProductList

    private fun getProduct() {
        viewModelScope.launch {
            repository.getProduct(selectedShop).collect { result ->
                when (result) {
                    is Result.Loading -> _status.value = LoadApiStatus.LOADING

                    is Result.Success -> {
                        result.data.let {
                            _branchProductList.value = it
                            _status.value = LoadApiStatus.DONE
                        }
                    }

                    is Result.Fail -> {
                        myToast(result.error, "long")
                        _status.value = LoadApiStatus.ERROR
                    }

                    is Result.Error -> Logger.e(result.exception.message.toString())
                }
            }
        }
    }

    fun filterProductList(productList: List<Product>) {

        val menuList = mutableListOf<Menu>()
        val titleList = mutableListOf<String>()

        for (i in productList.indices) {
            titleList.add(productList[i].type)
        }

        val newTitleList = titleList.distinct()

        for (i in newTitleList.indices) {
            val filteredProductList = productList.filter { it.type == newTitleList[i] }
            menuList.add(Menu.Title(newTitleList[i]))
            for (product in filteredProductList.indices) {
                menuList.add(Menu.MenuProduct(filteredProductList[product]))
            }
        }
        _menuList.value = menuList
    }

    fun navToDetail(product: Product) {
        _navToDetail.value = product
        _navToDetail.value = null
    }

    fun navToOrder() {
        _navToOrder.value = true
        _navToOrder.value = null
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }

    fun navToAddMenuItem() {
        _navToAddItem.value = true
        _navToAddItem.value = null
    }
}
