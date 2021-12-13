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

    // LiveData for observe menuList
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
    val popBack: LiveData<Boolean?>
        get() = _popBack

    // LiveData for observe user selected shop
    private val _shop = MutableLiveData<Shop?>().apply {
        value = selectedShop
    }
    val shop: LiveData<Shop?>
        get() = _shop

    // LiveData of productList in this shop
    private val _shopProductList = MutableLiveData<List<Product>>()
    val shopProductList: LiveData<List<Product>>
        get() = _shopProductList

    // LiveData for observe orderProductList
    private var _orderProductList = MutableLiveData<List<OrderProduct>>()
    val orderProductList: LiveData<List<OrderProduct>>
        get() = _orderProductList

    // LiveData for observe orderList
    private var _orderList = MutableLiveData<List<Order>>()
    val orderList: LiveData<List<Order>>
        get() = _orderList

    // LiveData for detect is this order has product or not
    private val _hasOrderProduct = MutableLiveData<Boolean>()
    val hasOrderProduct: LiveData<Boolean>
        get() = _hasOrderProduct

    // LiveData for detect this user has order or not
    private val _hasOrder = MutableLiveData<Boolean>()
    val hasOrder: LiveData<Boolean>
        get() = _hasOrder

    // LiveData for show dialog when user didn't create order for shop which user selected
    private val _showDialog = MutableLiveData<Boolean?>()
    val showDialog: LiveData<Boolean?>
        get() = _showDialog

    // LiveData of intent for user to share order to LINE
    private val _shareOrder = MutableLiveData<Intent?>()
    val shareOrder: LiveData<Intent?>
        get() = _shareOrder

    // LiveData for detect current state is [ Loading, Success, Fail or Error ]
    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    // LiveData for navController navigation to AddItem page
    private val _navToAddItem = MutableLiveData<Boolean?>()
    val navToAddItem: LiveData<Boolean?>
        get() = _navToAddItem

    // LiveData for observe user import order name
    var orderName = MutableLiveData<String>().apply {
        value = "我的訂單"
    }

    // Combine shopId & userId to create user orderId
    private val myId = selectedShop.shop_Id.substring(0, 10) + UserInfo.userId.substring(0, 10)

    // If otherUserId exist combine it with shopId to query and get otherUser order from firebase
    private val otherId: String
        get() {
            return selectedShop.shop_Id.substring(0, 10) + otherUserId?.substring(0, 10)
        }

    // Set myOrder data
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

    // Set intent for share orderId to LINE
    private fun shareOrderToLINE() {
        mOrder.order_Id = myId
        val lineUrl = "https://line.me/R/msg/text/快來跟我一起喝${selectedShop.name}吧！" +
                "shakeit://com.smalltsai.shakeit/${mOrder.order_Id}"
        val sendIntent = Intent.parseUri(lineUrl, Intent.URI_INTENT_SCHEME)
        _shareOrder.value = sendIntent
        _shareOrder.value = null
        _status.value = LoadApiStatus.DONE
    }

    /**
     * If user doesn't has order in [selectedShop] showDialog for create order
     * else start share order
     */
    fun startShare() {
        if (_hasOrder.value == false) {
            _showDialog.value = true
            _showDialog.value = null
        } else {
            _status.value = LoadApiStatus.LOADING
            shareOrderToLINE()
        }
    }

    /**
     * Post an order which has no product to firebase
     * if post success start sharing it to LINE
     */
    fun addNewOrderToFireBase() {
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
                    else -> {}
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

    // Update order total price when user add new product to order
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
                    else -> {}
                }
            }
        }
    }

    /**
     * When [MenuViewModel] created [getProduct], [getOrderDataByUserId], [getOrderProductByUserId] from firebase
     */
    fun initProduct() {
        getProduct()
        getOrderDataByUserId()
        getOrderProductByUserId()
    }

    /**
     * If [otherUserId] exist -> current order was created by otherUser -> getOrderData by [otherUserId]
     * Else -> current order was created by me -> getOrderData by [myId]
     */
    private fun getOrderDataByUserId() {
        _orderList = if (otherUserId != UserInfo.userId && otherUserId != "") {
            repository.getOrderByOrderId(otherId)
        } else {
            repository.getOrderByOrderId(myId)
        }
    }

    /**
     * If [otherUserId] exist -> order was created by otherUser -> getOrderProductData by [otherUserId]
     * Else -> current order was created by me -> getOrderProductData by [myId]
     */
    private fun getOrderProductByUserId() {
        _orderProductList = if (otherUserId != UserInfo.userId && otherUserId != "") {
            repository.getOrderProductBySnapShot(otherId)
        } else {
            repository.getOrderProductBySnapShot(myId)
        }
    }

    // Get product from selected shop
    private fun getProduct() {
        viewModelScope.launch {
            repository.getProduct(selectedShop).collect { result ->
                when (result) {
                    is Result.Loading -> _status.value = LoadApiStatus.LOADING

                    is Result.Success -> {
                        result.data.let {
                            _shopProductList.value = it
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

    /**
     * After [getProduct] -> map [productList] to it's type then filter product with each type
     * Double forLoop -> add productType then add product By [Menu] sealed class type
     * [Menu] class use for get different viewType in recycleView
     */
    fun filterProductList(productList: List<Product>) {

        val menuList = mutableListOf<Menu>()
        val productType = productList.map { it.type }.distinct()

        productType.forEach { type ->
            val filteredProductList = productList.filter { it.type == type }
            menuList.add(Menu.Title(type))
            filteredProductList.forEach { product ->
                menuList.add(Menu.MenuProduct(product))
            }
        }
        _menuList.value = menuList
    }

    // Set value for trigger observe then set null to it for prevent navController nav again
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
