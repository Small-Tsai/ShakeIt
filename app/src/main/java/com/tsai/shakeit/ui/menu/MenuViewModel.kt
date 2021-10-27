package com.tsai.shakeit.ui.menu

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ui.home.TAG
import kotlinx.coroutines.launch


class MenuViewModel(
    private val selectedShop: Shop,
    private val repository: ShakeItRepository,
    val orderId: String?
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

    fun updateOrderTotalPrice(totalPrice:Int){
        viewModelScope.launch {
            when(val result = repository.updateOrderTotalPrice(totalPrice,selectedShop.shop_Id)){
//                is Result.Success -> Log.d(TAG,"update total price")
            }

        }
    }

//    val data = Product(
//        "熟成紅茶",
//        "好香",
//        hashMapOf(
//            "大" to 35,
//            "中" to 30,
//        ),
//        arrayListOf("全糖", "半糖", "微糖", "無糖"),
//        arrayListOf("正常冰", "少冰", "微冰", "去冰"),
//        35,
//        hashMapOf(
//            "白玉珍珠" to 10,
//            "百香蒟蒻凍" to 20,
//            "水玉" to 10,
//            "桂玉" to 10
//        ),
//        selectedShop.shop_Id,
//        REDTEA,
//        shop_Name = "可不可熟成紅茶",
//        branch = "台北市政店",
//        shopAddress = "台北市信義區忠孝東路五段17之2號"
//    )

//    fun postProduct() {
//        viewModelScope.launch {
//            repository.postProduct(data)
//        }
//    }

    init {
        getProduct()
        _orderProduct = repository.getFireBaseOrderProduct(selectedShop.shop_Id)
    }

    private val _branchProduct = MutableLiveData<List<Product>>()
    val branchProduct: LiveData<List<Product>>
        get() = _branchProduct

    private fun getProduct() {
        viewModelScope.launch {
            when (val result = repository.getProduct(selectedShop.shop_Id)) {
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
                    Log.d(TAG, "getProduct Error")
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
}

