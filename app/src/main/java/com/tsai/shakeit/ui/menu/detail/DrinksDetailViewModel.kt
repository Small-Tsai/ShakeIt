package com.tsai.shakeit.ui.menu.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.launch

private const val ICE = "ice"
private const val CAPACITY = "capacity"
private const val SUGAR = "sugar"
private const val OTHERS = "others"


class DrinksDetailViewModel(
    val data: Product,
    private val repository: ShakeItRepository,
    private val shop: Shop?
) :
    ViewModel() {

    private val _product = MutableLiveData<List<DrinksDetail>>()
    val product: LiveData<List<DrinksDetail>>
        get() = _product

    private val _qty = MutableLiveData<Int>()
    val qty: LiveData<Int>
        get() = _qty

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    private val _refresh = MutableLiveData<Boolean?>()
    val refresh: LiveData<Boolean?>
        get() = _refresh

    var selectedPositionList = mutableListOf<Int>()
    var mContentList: MutableMap<String, ArrayList<String>> = mutableMapOf()

    fun addNewDocToFireBase() {

        val mOrder = Order(
            shop_Name = data.shop_Name,
            branch = shop!!.branch,
            date = Timestamp.now(),
            order_Name = "我的訂單",
            shop_Id = shop.shop_Id,
            user_Id = UserInfo.userId

            )

        val mOrderProduct = _qty.value?.let {
            OrderProduct(
                name = data.name,
                ice = mContentList[ICE]!!.first(),
                capacity = mContentList[CAPACITY]!!.first(),
                qty = it,
                sugar = mContentList[SUGAR]!!.first(),
                others = mContentList[OTHERS].toString()
                    .substring(1, mContentList[OTHERS].toString().length - 1),
                price = data.price,
                user_Name = UserInfo.userName,
                product_Img = data.product_Img
            )
        }

        viewModelScope.launch {
            mOrderProduct?.let {
                repository.postOrderToFireBase(mOrder, mOrderProduct)
            }
        }
    }

    init {
        _qty.value = 1
        filterList()
        mContentList[OTHERS] = arrayListOf("")
    }

    fun plus() {
        _qty.value = _qty.value?.plus(1)
    }

    fun minus() {
        _qty.value = _qty.value?.minus(1)
    }

    private fun filterList() {
        val detailList = mutableListOf<DrinksDetail>()
        detailList.add(DrinksDetail.DetailTitle("容量"))
        data.capacity.forEach { detailList.add(DrinksDetail.DetailContent(hashMapOf(it.key to it.value))) }
        detailList.add(DrinksDetail.DetailTitle("冰量"))
        data.ice.forEach { detailList.add(DrinksDetail.DetailContent(hashMapOf(it to 0))) }
        detailList.add(DrinksDetail.DetailTitle("甜度"))
        data.sugar.forEach { detailList.add(DrinksDetail.DetailContent(hashMapOf(it to 0))) }
        detailList.add(DrinksDetail.DetailTitle("加料"))
        data.others.forEach { detailList.add(DrinksDetail.DetailContent(hashMapOf(it.key to it.value))) }
        _product.value = detailList
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }

    var resultSize = 0
    private val capacitySize = data.capacity.size
    private val iceSize = data.ice.size
    private val sugarSize = data.sugar.size
    private val othersSize = data.others.size
    private val rangeCapacity = 1..capacitySize
    private val rangeCapacityToIce = selectRange(capacitySize, iceSize)
    private val rangeIceToSugar = selectRange(resultSize, sugarSize)
    private val rangeSugarToOthers = selectRange(resultSize, othersSize)
    private var firstClick = 0
    private var capacityPrice = 0
    private var othersPrice = 0

    fun doSelect(position: Int, content: String, price: Int) {

        if (firstClick == 0) {
            selectedPositionList.add(position); firstClick += 1
        }

        when (position) {
            in rangeCapacity -> {
                refactorPositionList(position, selectedPositionList, rangeCapacity, content)
                capacityPrice = price

            }
            in rangeCapacityToIce -> {
                refactorPositionList(position, selectedPositionList, rangeCapacityToIce, content)
            }
            in rangeIceToSugar -> {
                refactorPositionList(position, selectedPositionList, rangeIceToSugar, content)
            }
            in rangeSugarToOthers -> {
                othersPrice = price
                refactorListInOthersRange(position, content)
            }
        }
        data.price = capacityPrice + othersPrice
        _refresh.value = true
        _refresh.value = null
    }

    private fun refactorListInOthersRange(i: Int, content: String) {

        if (selectedPositionList.contains(i)) {
            mContentList[OTHERS]?.remove(content)
            selectedPositionList.remove(i)
//            Log.d(TAG, mContentList.toString())
        } else {
            selectedPositionList.add(i)

            if (mContentList[OTHERS] == arrayListOf("")) {
                mContentList[OTHERS] = arrayListOf(content)
            } else {
                mContentList[OTHERS]?.add(content)
            }
            Logger.d(mContentList.toString())
        }
    }

    private fun refactorPositionList(
        i: Int,
        list: MutableList<Int>,
        range: IntRange,
        content: String,
    ) {
        list.add(i)
        val mList = list.filter { it !in range } as MutableList<Int>
        mList.add(i)
        selectedPositionList = mList

        when (range) {
            rangeCapacity -> mContentList[CAPACITY] = arrayListOf(content)
            rangeCapacityToIce -> mContentList[ICE] = arrayListOf(content)
            rangeIceToSugar -> mContentList[SUGAR] = arrayListOf(content)
        }
//        Log.d(TAG, mContentList.toString())
    }

    private fun selectRange(lastSize: Int, nextSize: Int): IntRange {
        resultSize = lastSize + 1 + nextSize
        return lastSize + 2..resultSize
    }
}