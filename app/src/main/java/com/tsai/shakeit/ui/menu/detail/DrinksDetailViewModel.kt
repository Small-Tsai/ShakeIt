package com.tsai.shakeit.ui.menu.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.service.MyFireBaseService
import com.tsai.shakeit.ui.menu.detail.OptionsType.*
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DrinksDetailViewModel(
    val data: Product,
    private val repository: ShakeItRepository,
    private val shop: Shop?,
    private val otherUserId: String?,
    private val hasOrder: Boolean?,
) : ViewModel() {

    private val _drinksDetailList = MutableLiveData<List<DrinksDetail>>()
    val drinksDetailList: LiveData<List<DrinksDetail>>
        get() = _drinksDetailList

    private val _qty = MutableLiveData<Int>().apply { value = 1 }
    val qty: LiveData<Int>
        get() = _qty

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    private val _showDialog = MutableLiveData<Boolean?>()
    val showDialog: LiveData<Boolean?>
        get() = _showDialog

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    var orderName = MutableLiveData<String>().apply { value = "我的訂單" }

    private val isAllSelected = MutableLiveData<Boolean>().apply { value = false }
    val isCapacitySelected = MutableLiveData<Boolean>().apply { value = false }
    val isIceSelected = MutableLiveData<Boolean>().apply { value = false }
    val isSugarSelected = MutableLiveData<Boolean>().apply { value = false }
    var unSelectText = MutableLiveData<String>()

    var selectedPositionList = mutableListOf<Pair<Int, Int>>()
    private var productDetailContentList: MutableMap<Int, ArrayList<String>> = mutableMapOf()

    fun showDialog() {
        if (hasOrder == false && isAllSelected.value == true) {
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
            shop_Name = data.shop_Name.last(),
            branch = shop!!.branch,
            date = Timestamp.now(),
            order_Name = orderName.value!!,
            shop_Id = shop.shop_Id,
            user_Id = UserInfo.userId,
            invitation = arrayListOf(UserInfo.userId),
            shop_Img = shop.shop_Img
        )

        val mOrderProduct = _qty.value?.let { qty ->
            productDetailContentList[ICE.type].let { ice ->
                productDetailContentList[CAPACITY.type]?.let { capacity ->
                    productDetailContentList[OTHERS.type]?.let { others ->
                        OrderProduct(
                            name = data.name,
                            ice = ice?.first() ?: "無法調冰",
                            capacity = capacity.first(),
                            qty = qty,
                            sugar = productDetailContentList[SUGAR.type]?.first() ?: "無法調甜",
                            others = others.toString()
                                .substring(
                                    1,
                                    productDetailContentList[OTHERS.type].toString().length - 1
                                ),
                            price = data.price,
                            product_Img = data.product_Img,
                            user = User(
                                user_Id = UserInfo.userId,
                                user_Image = UserInfo.userImage,
                                user_Name = UserInfo.userName,
                                user_Token = MyFireBaseService.token.toString()
                            ),
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            checkIsEveryOptionSelected()
            postAfterInternetChecked(mOrderProduct, mOrder)
        }
    }

    private suspend fun postAfterInternetChecked(
        mOrderProduct: OrderProduct?,
        order: Order,
    ) {
        if (
            isIceSelected.value == true &&
            isCapacitySelected.value == true &&
            isSugarSelected.value == true
        ) {
            mOrderProduct?.let { orderProduct ->
                otherUserId?.let { otherUserId ->
                    hasOrder?.let { hasOrder ->
                        repository.postOrderToFireBase(order, orderProduct, otherUserId, hasOrder)
                            .collect { result ->
                                when (result) {
                                    is Result.Loading -> _status.value = LoadApiStatus.LOADING
                                    is Result.Success -> {
                                        closeDialog()
                                        popBack()
                                        myToast("加入訂單成功")
                                        _status.value = LoadApiStatus.DONE
                                    }
                                    is Result.Fail -> myToast(result.error)
                                    is Result.Error -> Logger.e(result.exception.toString())
                                }
                            }
                    }
                }
            }
        }
    }

    private fun checkIsEveryOptionSelected() {

        if (productDetailContentList[ICE.type].isNullOrEmpty() && data.ice.size > 1) {
            unSelectText.value = "尚未選擇冰量"
        } else {
            isIceSelected.value = true
        }

        if (productDetailContentList[CAPACITY.type].isNullOrEmpty()) {
            unSelectText.value = "尚未選擇容量"
        }

        if (productDetailContentList[SUGAR.type].isNullOrEmpty() && data.sugar.size > 1) {
            unSelectText.value = "尚未選擇甜度"
        } else {
            isSugarSelected.value = true
        }
    }

    init {
        refactorDetailList()
        productDetailContentList[OTHERS.type] = arrayListOf("")
    }

    fun plus() {
        _qty.value = _qty.value?.plus(1)
    }

    fun minus() {
        _qty.value = _qty.value?.minus(1)
    }

    private fun refactorDetailList() {

        val detailList = mutableListOf<DrinksDetail>()

        detailList.add(DrinksDetail.DetailTitle("容量"))
        data.capacity.forEach {
            detailList.add(
                DrinksDetail.DetailContent(
                    hashMapOf(it.key to it.value),
                    CAPACITY.type
                )
            )
        }

        if (data.ice.size > 1) {
            detailList.add(DrinksDetail.DetailTitle("冰量"))
            data.ice.forEach {
                detailList.add(
                    DrinksDetail.DetailContent(
                        hashMapOf(it.key to it.value),
                        ICE.type
                    )
                )
            }
        }

        if (data.sugar.size > 1) {
            detailList.add(DrinksDetail.DetailTitle("甜度"))
            data.sugar.forEach {
                detailList.add(
                    DrinksDetail.DetailContent(
                        hashMapOf(it.key to it.value),
                        SUGAR.type
                    )
                )
            }
        }

        if (data.others.size != 0) {
            detailList.add(DrinksDetail.DetailTitle("加料"))
            data.others.forEach {
                detailList.add(
                    DrinksDetail.DetailContent(
                        hashMapOf(it.key to it.value),
                        OTHERS.type
                    )
                )
            }
        }

        _drinksDetailList.value = detailList
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }

    private var capacityPrice = 0
    private var othersPrice = 0
    fun doSelect(position: Int, content: String, price: Int, type: Int) {

        when (type) {
            CAPACITY.type -> {
                isCapacitySelected.value = true
                refactorPositionList(position, selectedPositionList, CAPACITY.type, content)
                capacityPrice = price
            }
            ICE.type -> {
                isIceSelected.value = true
                refactorPositionList(position, selectedPositionList, ICE.type, content)
            }
            SUGAR.type -> {
                isSugarSelected.value = true
                refactorPositionList(position, selectedPositionList, SUGAR.type, content)
            }
            OTHERS.type -> {
                othersPrice = price
                refactorListInOthersRange(position, content)
            }
        }
        data.price = capacityPrice + othersPrice
        _drinksDetailList.value = _drinksDetailList.value
    }

    private fun refactorListInOthersRange(position: Int, content: String) {

        if (selectedPositionList.map { it.first }.contains(position)) {
            productDetailContentList[OTHERS.type]?.remove(content)
            selectedPositionList.remove(Pair(position, OTHERS.type))
        } else {
            selectedPositionList.add(Pair(position, OTHERS.type))

            if (productDetailContentList[OTHERS.type] == arrayListOf("")) {
                productDetailContentList[OTHERS.type] = arrayListOf(content)
            } else {
                productDetailContentList[OTHERS.type]?.add(content)
            }
        }
    }

    private fun refactorPositionList(
        position: Int,
        selectedPositionList: MutableList<Pair<Int, Int>>,
        type: Int,
        content: String,
    ) {
        val newSelectedPositionList =
            selectedPositionList.filter { it.second != type } as MutableList<Pair<Int, Int>>
        newSelectedPositionList.add(Pair(position, type))
        this.selectedPositionList = newSelectedPositionList

        when (type) {
            CAPACITY.type -> productDetailContentList[type] = arrayListOf(content)
            ICE.type -> productDetailContentList[type] = arrayListOf(content)
            SUGAR.type -> productDetailContentList[type] = arrayListOf(content)
        }
    }
}
