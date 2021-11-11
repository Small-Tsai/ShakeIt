package com.tsai.shakeit.ui.menu.detail

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.util.*
import kotlinx.coroutines.launch

class DrinksDetailViewModel(
    val data: Product,
    private val repository: ShakeItRepository,
    private val shop: Shop?,
    private val otherUserId: String?,
    private val orderSize: Int?
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

    val isCapacitySelected = MutableLiveData<Boolean>().apply { value = true }
    val isIceSelected = MutableLiveData<Boolean>().apply { value = true }
    val isSugarSelected = MutableLiveData<Boolean>().apply { value = true }
    var unSelectText = MutableLiveData<String>()

    private val _showDialog = MutableLiveData<Boolean?>()
    val showDialog: LiveData<Boolean?>
        get() = _showDialog

    var title = MutableLiveData<String>().apply {
        value = "我的訂單"
    }

    fun showDialog() {
        if (orderSize == 0) {
            _showDialog.value = true
            _showDialog.value = null
        } else {
            addNewDocToFireBase()
        }
    }

    private fun closeDialog() {
        _showDialog.value = false
    }

    fun addNewDocToFireBase() {

        val mOrder = Order(
            shop_Name = data.shop_Name,
            branch = shop!!.branch,
            date = Timestamp.now(),
            order_Name = title.value!!,
            shop_Id = shop.shop_Id,
            user_Id = UserInfo.userId,
            invitation = arrayListOf(UserInfo.userId),
            shop_Img = shop.shop_Img
        )

        val mOrderProduct = _qty.value?.let { qty ->
            mContentList[ICE]?.let { ice ->
                mContentList[CAPACITY]?.let { capacity ->
                    mContentList[OTHERS]?.let { others ->
                        mContentList[SUGAR]?.let { sugar ->
                            OrderProduct(
                                name = data.name,
                                ice = ice.first(),
                                capacity = capacity.first(),
                                qty = qty,
                                sugar = sugar.first(),
                                others = others.toString()
                                    .substring(1, mContentList[OTHERS].toString().length - 1),
                                price = data.price,
                                product_Img = data.product_Img,
                                user = User(
                                    user_Id = UserInfo.userId,
                                    user_Image = UserInfo.userImage,
                                    user_Name = UserInfo.userName
                                ),
                            )
                        }
                    }
                }
            }
        }

        viewModelScope.launch {

            if (mContentList[ICE].isNullOrEmpty()) {
                mToast("尚未選擇冰量")
                isIceSelected.value = false
                unSelectText.value = "尚未選擇冰量"
            } else {
                isIceSelected.value = true
            }
            if (mContentList[CAPACITY].isNullOrEmpty()) {
                mToast("尚未選擇容量")
                isCapacitySelected.value = false
                unSelectText.value = "尚未選擇容量"
            } else {
                isCapacitySelected.value = true
            }
            if (mContentList[SUGAR].isNullOrEmpty()) {
                mToast("尚未選擇甜度")
                isSugarSelected.value = false
                unSelectText.value = "尚未選擇甜度"
            } else {
                isSugarSelected.value = true
            }

            mOrderProduct?.let { mOrderProduct ->
                otherUserId?.let { otherUserId ->
                    orderSize?.let { orderSize ->
                        when (val result = repository.postOrderToFireBase(
                            mOrder,
                            mOrderProduct,
                            otherUserId,
                            orderSize
                        )) {
                            is Result.Success -> {
                                closeDialog()
                                popBack()
                                mToast("加入訂單成功")
                            }
                        }
                    }
                }
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
        data.ice.forEach { detailList.add(DrinksDetail.DetailContent(hashMapOf(it.key to it.value))) }
        detailList.add(DrinksDetail.DetailTitle("甜度"))
        data.sugar.forEach { detailList.add(DrinksDetail.DetailContent(hashMapOf(it.key to it.value))) }
        detailList.add(DrinksDetail.DetailTitle("加料"))
        data.others.forEach { detailList.add(DrinksDetail.DetailContent(hashMapOf(it.key to it.value))) }
        _product.value = detailList
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }

    private var resultSize = 0
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
        } else {
            selectedPositionList.add(i)

            if (mContentList[OTHERS] == arrayListOf("")) {
                mContentList[OTHERS] = arrayListOf(content)
            } else {
                mContentList[OTHERS]?.add(content)
            }
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
    }

    private fun selectRange(lastSize: Int, nextSize: Int): IntRange {
        resultSize = lastSize + 1 + nextSize
        return lastSize + 2..resultSize
    }
}