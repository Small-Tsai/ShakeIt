package com.tsai.shakeit.ui.menu

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

    private var _order = MutableLiveData<List<Order>>()
    val order: LiveData<List<Order>>
        get() = _order

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

    var title = MutableLiveData<String>().apply {
        value = "我的訂單"
    }

    private val myId = selectedShop.shop_Id.substring(0, 10) + UserInfo.userId.substring(0, 10)
    private var otherId = ""

    val mOrder = Order(
        shop_Name = selectedShop.name,
        branch = selectedShop.branch,
        date = Timestamp.now(),
        order_Name = title.value!!,
        shop_Id = selectedShop.shop_Id,
        user_Id = UserInfo.userId,
        invitation = arrayListOf(UserInfo.userId),
        shop_Img = selectedShop.shop_Img
    )

    private fun shareOrderToLINE() {
        mOrder.order_Id = myId
        val lineUrl = "https://line.me/R/msg/text/https://com.smalltsai.shakeit/${mOrder.order_Id}"
        val sendIntent = Intent.parseUri(lineUrl, Intent.URI_INTENT_SCHEME)
        _shareOrder.value = sendIntent
        _shareOrder.value = null
    }

    fun startShare() {
        if (_hasOrder.value == false) {
            _showDialog.value = true
            _showDialog.value = null
        } else {
            shareOrderToLINE()
        }
    }

    fun addNewDocToFireBase() {
        viewModelScope.launch {
            _status.value = LoadApiStatus.LOADING
            mOrder.order_Name = title.value.toString()
            when (val result =
                withContext(Dispatchers.IO) { repository.crateNewOrderForShare(mOrder) }) {
                is Result.Success -> {
                    shareOrderToLINE()
                    _showDialog.value = false
                    _status.value = LoadApiStatus.DONE
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
                when (val result =
                    repository.updateOrderTotalPrice(totalPrice, selectedShop.shop_Id, it)) {
//                is Result.Success -> Log.d(TAG,"update total price")
                }
            }
        }
    }

    init {
        getProduct()
        getOrder()
        getOrderProduct()
    }

    private fun getOrder() {
        if (otherUserId != UserInfo.userId && otherUserId != "") {
            otherId = selectedShop.shop_Id.substring(0, 10) + otherUserId?.substring(0, 10)
            _order = repository.getShopOrder(otherId)
        } else {
            _order = repository.getShopOrder(myId)
        }
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

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.getProduct(selectedShop.name)) {
                is Result.Success -> {
                    _branchProduct.value = result.data!!
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    mToast("商品獲取失敗請檢查是否開啟網路", "long")
                    _status.value = LoadApiStatus.ERROR
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

