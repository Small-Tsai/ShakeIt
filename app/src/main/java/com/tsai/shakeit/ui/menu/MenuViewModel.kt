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

const val REDTEA = "紅茶"
const val GREENTEA = "綠茶"
const val WULONG = "烏龍茶"

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

    private var _userOrderList = MutableLiveData<List<Order>>()
    val userOrderList: LiveData<List<Order>>
        get() = _userOrderList

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

    private val mockData =
        listOf(
            Product(
                "熟成紅茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                REDTEA,
                shop_Name = "茶湯會",
                branch = "公館商圈店",
            ),
            Product(
                "熟成綠茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                GREENTEA,
                shop_Name = "茶湯會",
                branch = "公館商圈店",

                ),
            Product(
                "熟成紅茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                REDTEA,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成紅茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                REDTEA,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成烏龍茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                WULONG,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成紅茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                REDTEA,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成烏龍茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                WULONG,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成綠茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                GREENTEA,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成烏龍茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                WULONG,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成綠茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                GREENTEA,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成烏龍茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                WULONG,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
            Product(
                "熟成烏龍茶",
                "好香",
                arrayListOf("大", "中"),
                arrayListOf("全糖", "半糖", "微糖", "無糖"),
                arrayListOf("正常冰", "少冰", "微冰", "去冰"),
                35,
                arrayListOf("加珍珠", "加椰果"),
                selectedShop.shop_Id,
                WULONG,
                shop_Name = "茶湯會",
                branch = "公館商圈店"
            ),
        )

    init {
        filterMyList()
        _orderProduct = repository.getFireBaseOrderProduct(selectedShop.shop_Id)
    }

    fun filterMyList() {

        val mList = mutableListOf<Menu>()
        val titleList = mutableListOf<String>()

        for (i in mockData.indices) {
            titleList.add(mockData[i].type)
        }

        val newTitleList = titleList.distinct()

        for (i in newTitleList.indices) {
            val list = mockData.filter { it.type == newTitleList[i] }
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

