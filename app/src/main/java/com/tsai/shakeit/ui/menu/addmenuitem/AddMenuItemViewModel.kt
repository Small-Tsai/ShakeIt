package com.tsai.shakeit.ui.menu.addmenuitem

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.ui.menu.detail.OptionsType
import com.tsai.shakeit.ui.menu.detail.OptionsType.*
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMenuItemViewModel(
    private val repository: ShakeItRepository,
    val shop: Shop?
) : ViewModel() {

    private val _addCapacityListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value = mutableListOf()
    }
    val addCapacityListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addCapacityListLiveData

    private val _addIceListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value = mutableListOf()
    }
    val addIceListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addIceListLiveData

    private val _addSugarListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value = mutableListOf()
    }
    val addSugarListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addSugarListLiveData

    private val _addOtherListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value = mutableListOf()
    }
    val addOtherListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addOtherListLiveData

    private val _productFireBaseImageUri = MutableLiveData<String>()

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    private val _navToMenu = MutableLiveData<Boolean?>()
    val navToMenu: LiveData<Boolean?>
        get() = _navToMenu

    val optionName = MutableLiveData<String>()
    val optionPrice = MutableLiveData<String>()
    var productName = ""
    var productDescription = ""
    var productType = ""

    private val _capacityListForPost = MutableLiveData<HashMap<String, Int>>()
    private val _iceListForPost = MutableLiveData<HashMap<String, Int>>()
    private val _sugarListForPost = MutableLiveData<HashMap<String, Int>>()
    private val _othersListForPost = MutableLiveData<HashMap<String, Int>>()

    private var capacityList = mutableListOf<AddMenuItem>()
    private var iceList = mutableListOf<AddMenuItem>()
    private var sugarList = mutableListOf<AddMenuItem>()
    private var othersList = mutableListOf<AddMenuItem>()

    init {
        _capacityListForPost.value = hashMapOf()
        _iceListForPost.value = hashMapOf()
        _sugarListForPost.value = hashMapOf()
        _othersListForPost.value = hashMapOf()
    }

    // type 0->capacity , 1->ice , 2->sugar , 3->other
    fun initSelectItem() {

        capacityList = mutableListOf(
            AddMenuItem.Title("容量選項"),
            AddMenuItem.Detail(hashMapOf(), 0),
            AddMenuItem.Button(0)
        )

        iceList = mutableListOf(
            AddMenuItem.Title("冰量選項"),
            AddMenuItem.Detail(hashMapOf(), 1),
            AddMenuItem.Button(1)
        )

        sugarList =
            mutableListOf(
                AddMenuItem.Title("甜度選項"),
                AddMenuItem.Detail(hashMapOf(), 2),
                AddMenuItem.Button(2)
            )

        othersList =
            mutableListOf(
                AddMenuItem.Title("加料選項"),
                AddMenuItem.Detail(hashMapOf(), 3),
                AddMenuItem.Button(3)
            )

        refreshLiveData()
    }


    private var currentSelectedPostion = -1
    fun recordCurrentSelectedPosition(positon: Int) {
        Logger.d("cu = $positon")
        currentSelectedPostion = positon
    }

    private var currentSelectedType = -1
    fun recordCurrentSelectedType(type: Int) {
        currentSelectedType = type
    }

    //record editText content
    private val capacityOptionContent = hashMapOf<Int, String?>()
    private val iceOptionContent = hashMapOf<Int, String?>()
    private val sugarOptionContent = hashMapOf<Int, String?>()
    private val othersOptionContent = hashMapOf<Int, String?>()

    fun setListContent(userImportContent: String) {
        when (currentSelectedType) {
            0 -> {
                if (userImportContent.isNotEmpty()) {
                    capacityOptionContent[currentSelectedPostion] = userImportContent
                } else {
                    capacityOptionContent[currentSelectedPostion] = null
                }
                Logger.d("capacityOptionContent = $capacityOptionContent")
            }
            1 -> {
                if (userImportContent.isNotEmpty()) {
                    iceOptionContent[currentSelectedPostion] = userImportContent
                } else {
                    iceOptionContent[currentSelectedPostion] = null
                }
                Logger.d("iceOptionContent = $iceOptionContent")
            }
            2 -> {
                if (userImportContent.isNotEmpty()) {
                    sugarOptionContent[currentSelectedPostion] = userImportContent
                } else {
                    sugarOptionContent[currentSelectedPostion] = null
                }
                Logger.d("sugarOptionContent = $sugarOptionContent")
            }
            3 -> {
                if (userImportContent.isNotEmpty()) {
                    othersOptionContent[currentSelectedPostion] = userImportContent
                } else {
                    othersOptionContent[currentSelectedPostion] = null
                }
                Logger.d("othersOptionContent = $othersOptionContent")
            }
        }
    }

    //record editText price
    private val capacityOptionPrice = hashMapOf<Int, String>()
    private val othersOptionPrice = hashMapOf<Int, String>()
    fun setListPrice(price: String) {
        if (price.isNotEmpty()) {
            when (currentSelectedType) {
                0 -> capacityOptionPrice[currentSelectedPostion] = price
                3 -> othersOptionPrice[currentSelectedPostion] = price
            }
        }
    }

    fun implementCommonOption() {
        clearAllOption()
        setNewOptions()
        setNewPostData()
        refreshLiveData()
    }

    private fun clearAllOption() {
        currentSelectedPostion = -1
        capacityList.clear()
        iceList.clear()
        sugarList.clear()
        othersList.clear()
        capacityOptionContent.clear()
        iceOptionContent.clear()
        sugarOptionContent.clear()
        othersOptionContent.clear()
    }

    private fun setNewOptions() {
        capacityList = mutableListOf(
            AddMenuItem.Title("容量選項"),
            AddMenuItem.Detail(hashMapOf("大" to 0), 0),
            AddMenuItem.Button(0)
        )

        iceList =
            mutableListOf(
                AddMenuItem.Title("冰量選項"),
                AddMenuItem.Detail(hashMapOf("正常冰" to 0), 1),
                AddMenuItem.Detail(hashMapOf("少冰" to 0), 1),
                AddMenuItem.Detail(hashMapOf("微冰" to 0), 1),
                AddMenuItem.Detail(hashMapOf("去冰" to 0), 1),
                AddMenuItem.Detail(hashMapOf("常溫" to 0), 1),
                AddMenuItem.Button(1)
            )

        sugarList =
            mutableListOf(
                AddMenuItem.Title("甜度選項"),
                AddMenuItem.Detail(hashMapOf("全糖" to 0), 2),
                AddMenuItem.Detail(hashMapOf("7分糖" to 0), 2),
                AddMenuItem.Detail(hashMapOf("5分糖" to 0), 2),
                AddMenuItem.Detail(hashMapOf("3分糖" to 0), 2),
                AddMenuItem.Detail(hashMapOf("1分糖" to 0), 2),
                AddMenuItem.Detail(hashMapOf("無糖" to 0), 2),
                AddMenuItem.Button(2)
            )

        othersList =
            mutableListOf(
                AddMenuItem.Title("加料選項"),
                AddMenuItem.Detail(hashMapOf("珍珠" to 10), 3),
                AddMenuItem.Detail(hashMapOf("蘆薈" to 10), 3),
                AddMenuItem.Detail(hashMapOf("椰果" to 10), 3),
                AddMenuItem.Button(3)
            )
    }

    private fun setNewPostData() {
        capacityOptionContent[1] = "大"
        iceOptionContent[1] = "正常冰"
        iceOptionContent[2] = "少冰"
        iceOptionContent[3] = "微冰"
        iceOptionContent[4] = "去冰"
        sugarOptionContent[1] = "全糖"
        sugarOptionContent[2] = "7分糖"
        sugarOptionContent[3] = "5分糖"
        sugarOptionContent[4] = "3分糖"
        sugarOptionContent[5] = "1分糖"
        sugarOptionContent[6] = "無糖"
        othersOptionContent[1] = "珍珠"
        othersOptionContent[2] = "蘆薈"
        othersOptionContent[3] = "椰果"
        othersOptionPrice[1] = "10"
        othersOptionPrice[2] = "10"
        othersOptionPrice[3] = "10"
    }


    //merge content and price
    fun mergeAllList() {

        if (!Util.isInternetConnected()) {
            _status.value = LoadApiStatus.ERROR
            myToast(Util.getString(R.string.internet_not_connected))
        } else if (capacityOptionContent.isNullOrEmpty() || capacityOptionPrice.isNullOrEmpty()) {
            myToast("請至少填寫一組容量與價格選項")
        } else if (iceOptionContent.isNullOrEmpty()) {
            myToast("請至少填寫一組冰量選項")
        } else if (sugarOptionContent.isNullOrEmpty()) {
            myToast("請至少填寫一組甜度選項")
        } else if (productName.isEmpty() || productType.isEmpty()) {
            myToast("商品名稱與類別不可留白喔")
        } else {
            viewModelScope.launch {

                //post image first
                postImgUriToFireBase()

                capacityOptionContent.keys.forEach {
                    if (!capacityOptionContent[it].isNullOrEmpty() && it > 0) {
                        _capacityListForPost.value?.set(
                            capacityOptionContent[it] ?: "",
                            capacityOptionPrice[it]?.toInt() ?: 0
                        )
                    }
                }

                iceOptionContent.keys.forEach {
                    if (!iceOptionContent[it].isNullOrEmpty()) {
                        _iceListForPost.value?.set(
                            iceOptionContent[it] ?: "",
                            0
                        )
                    }
                }

                sugarOptionContent.keys.forEach {
                    if (!sugarOptionContent[it].isNullOrEmpty()) {
                        _sugarListForPost.value?.set(
                            sugarOptionContent[it] ?: "", 0
                        )
                    }
                }

                othersOptionContent.keys.forEach {
                    if (!othersOptionContent[it].isNullOrEmpty())
                        _othersListForPost.value?.set(
                            othersOptionContent[it] ?: "",
                            othersOptionPrice[it]?.toInt() ?: 0
                        )
                }

                val mArray = arrayListOf<String>()
                var mString = ""

                for (i in shop!!.name.indices) {
                    mString += shop.name[i].toString()
                    mArray.add(mString)
                }

                //set product data
                val product = Product(
                    name = productName.replace(" ", ""),
                    content = productDescription,
                    capacity = _capacityListForPost.value!!,
                    ice = _iceListForPost.value!!,
                    sugar = _sugarListForPost.value!!,
                    others = _othersListForPost.value!!,
                    shopId = shop.shop_Id,
                    shopAddress = shop.address,
                    shop_Name = mArray,
                    branch = shop.branch.replace(" ", ""),
                    type = productType,
                    product_Img = _productFireBaseImageUri.value.toString()
                )
                Logger.d("product = $product")

                if (_productFireBaseImageUri.value != null) {
                    //post product data to firebase
                    when (val result = withContext(Dispatchers.IO) {
                        repository.postProduct(product)
                    }) {
                        is Result.Success -> {
                            myToast("上傳商品成功", "long")
                            _status.value = LoadApiStatus.DONE
                            _navToMenu.value = true
                            _navToMenu.value = null
                        }
                        is Result.Fail -> {
                            _status.value = LoadApiStatus.ERROR
                        }
                    }
                }
            }
        }
    }

    //use to post product img
    val productImageUri = MutableLiveData<Uri>()

    private suspend fun postImgUriToFireBase() {

        viewModelScope.async {

            if (productImageUri.value == null) {
                myToast("請上傳一張商品圖片")
            } else {
                _status.value = LoadApiStatus.LOADING
                productImageUri.value?.let {
                    when (val result = withContext(Dispatchers.IO) {
                        repository.postImage(it)
                    }) {
                        is Result.Success -> {
                            _productFireBaseImageUri.value = result.data!!
                        }
                        is Result.Fail -> {
                            myToast(result.error, "long")
                            _status.value = LoadApiStatus.ERROR
                        }
                    }
                }
            }
        }.await()
    }

    // when onclick addBtn add new list for user to set product data
    fun onClick(type: Int) {
        Logger.d(type.toString())
        when (type) {
            0 -> addNewOption(capacityList, CAPACITY.type)
            1 -> addNewOption(iceList, ICE.type)
            2 -> addNewOption(sugarList, SUGAR.type)
            3 -> addNewOption(othersList, OTHERS.type)
        }
        refreshLiveData()
    }

    private fun addNewOption(optionsList: MutableList<AddMenuItem>, optionType: Int) {
        currentSelectedType = optionType
        currentSelectedPostion = optionsList.lastIndex
        optionsList.add(optionsList.lastIndex, AddMenuItem.Detail(hashMapOf(), optionType))
    }

    private fun refreshLiveData() {
        _addCapacityListLiveData.value = capacityList
        _addIceListLiveData.value = iceList
        _addSugarListLiveData.value = sugarList
        _addOtherListLiveData.value = othersList
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }
}