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
import com.tsai.shakeit.databinding.AddMenuItemRowBinding
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class AddMenuItemViewModel(
    private val repository: ShakeItRepository,
    val shop: Shop?
) : ViewModel() {

    private val _addCapacityListLiveData = MutableLiveData<List<AddMenuItem>>()
    val addCapacityListLiveData: LiveData<List<AddMenuItem>>
        get() = _addCapacityListLiveData

    private val _addIceListLiveData = MutableLiveData<List<AddMenuItem>>()
    val addIceListLiveData: LiveData<List<AddMenuItem>>
        get() = _addIceListLiveData

    private val _addSugarListLiveData = MutableLiveData<List<AddMenuItem>>()
    val addSugarListLiveData: LiveData<List<AddMenuItem>>
        get() = _addSugarListLiveData

    private val _addOtherListLiveData = MutableLiveData<List<AddMenuItem>>()
    val addOtherListLiveData: LiveData<List<AddMenuItem>>
        get() = _addOtherListLiveData

    private val _productFireBaseImageUri = MutableLiveData<String>()
    private val productFireBaseImageUri: LiveData<String>
        get() = _productFireBaseImageUri

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    private val _navToMenu = MutableLiveData<Boolean?>()
    val navToMenu: LiveData<Boolean?>
        get() = _navToMenu

    val content = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    var name = ""
    var dercription = ""
    var type = ""

    private val _capacityList = MutableLiveData<HashMap<String, Int>>()
    private val _iceList = MutableLiveData<HashMap<String, Int>>()
    private val _sugarList = MutableLiveData<HashMap<String, Int>>()
    private val _others = MutableLiveData<HashMap<String, Int>>()

    init {
        _capacityList.value = hashMapOf()
        _iceList.value = hashMapOf()
        _sugarList.value = hashMapOf()
        _others.value = hashMapOf()
    }

    private var addCapacityList = mutableListOf<AddMenuItem>()
    private var addIceList = mutableListOf<AddMenuItem>()
    private var addSugarList = mutableListOf<AddMenuItem>()
    private var addOtherList = mutableListOf<AddMenuItem>()

    // type 0->capacity , 1->ice , 2->sugar , 3->other
    fun initSelectItem() {
        addCapacityList.add(AddMenuItem.Title("容量選項"))
        addCapacityList.add(AddMenuItem.Detail(hashMapOf(), 0))
        addCapacityList.add(AddMenuItem.Button(0))
        addIceList.add(AddMenuItem.Title("冰量選項"))
        addIceList.add(AddMenuItem.Detail(hashMapOf(), 1))
        addIceList.add(AddMenuItem.Button(1))
        addSugarList.add(AddMenuItem.Title("甜度選項"))
        addSugarList.add(AddMenuItem.Detail(hashMapOf(), 2))
        addSugarList.add(AddMenuItem.Button(2))
        addOtherList.add(AddMenuItem.Title("加料選項"))
        addOtherList.add(AddMenuItem.Detail(hashMapOf(), 3))
        addOtherList.add(AddMenuItem.Button(3))
        _addCapacityListLiveData.value = addCapacityList
        _addIceListLiveData.value = addIceList
        _addSugarListLiveData.value = addSugarList
        _addOtherListLiveData.value = addOtherList
    }

    var currentSelectedPostion = -1
    fun recordCurrentSelectedPosition(positon: Int) {
        currentSelectedPostion = positon
    }

    var currentSelectedType = -1
    fun recordCurrentSelectedType(type: Int) {
        currentSelectedType = type
    }

    //record editText content
    private val userCapaContentList = hashMapOf<Int, String>()
    private val userIceContentList = hashMapOf<Int, String>()
    private val userSugarContentList = hashMapOf<Int, String>()
    private val userOtherContentList = hashMapOf<Int, String>()
    fun setListContent(contnet: String) {
        if (contnet.isNotEmpty()) {
            when (currentSelectedType) {
                0 -> userCapaContentList[currentSelectedPostion] = contnet
                1 -> {
                    userIceContentList[currentSelectedPostion] = contnet
                    Logger.d("$userIceContentList")
                }
                2 -> userSugarContentList[currentSelectedPostion] = contnet
                3 -> userOtherContentList[currentSelectedPostion] = contnet
            }
        }
    }

    //record editText price
    private val userCapaPriceList = hashMapOf<Int, String>()
    private val userOtherPriceList = hashMapOf<Int, String>()
    fun setListPrice(price: String) {
        if (price.isNotEmpty()) {
            when (currentSelectedType) {
                0 -> userCapaPriceList[currentSelectedPostion] = price
                3 -> userOtherPriceList[currentSelectedPostion] = price
            }
        }
    }

    //merge content and price
    fun mergeAllList() {

        if (!Util.isInternetConnected()) {
            _status.value = LoadApiStatus.ERROR
            mToast(Util.getString(R.string.internet_not_connected))
        } else if (userCapaContentList.isNullOrEmpty() || userCapaPriceList.isNullOrEmpty()) {
            mToast("請至少填寫一組容量與價格選項")
        } else if (userIceContentList.isNullOrEmpty()) {
            mToast("請至少填寫一組冰量選項")
        } else if (userSugarContentList.isNullOrEmpty()) {
            mToast("請至少填寫一組甜度選項")
        } else if (name.isEmpty() || type.isEmpty()) {
            mToast("商品名稱與類別不可留白喔")
        } else {
            viewModelScope.launch {

                //post image first
                postImgUriToFireBase()

                userCapaContentList.keys.forEach {
                    if (!userCapaPriceList[it].isNullOrEmpty()) {
                        _capacityList.value?.set(
                            userCapaContentList[it] ?: "",
                            userCapaPriceList[it]?.toInt() ?: 0
                        )
                    }
                }
                userIceContentList.keys.forEach {
                    _iceList.value?.set(
                        userIceContentList[it] ?: "",
                        0
                    )
                }
                userSugarContentList.keys.forEach {
                    _sugarList.value?.set(
                        userSugarContentList[it] ?: "", 0
                    )
                }
                userOtherContentList.keys.forEach {
                    if (!userOtherPriceList[it].isNullOrEmpty())
                        _others.value?.set(
                            userOtherContentList[it] ?: "",
                            userOtherPriceList[it]?.toInt() ?: 0
                        )
                }

                //set product data
                val product = Product(
                    name = name,
                    content = dercription,
                    capacity = _capacityList.value!!,
                    ice = _iceList.value!!,
                    sugar = _sugarList.value!!,
                    others = _others.value!!,
                    shopId = shop!!.shop_Id,
                    shopAddress = shop.address,
                    shop_Name = shop.name,
                    branch = shop.branch,
                    type = type,
                    product_Img = _productFireBaseImageUri.value.toString()
                )
                Logger.d("product = $product")

                if (_productFireBaseImageUri.value != null) {
                    //post product data to firebase
                    when (val result = withContext(Dispatchers.IO) {
                        repository.postProduct(product)
                    }) {
                        is Result.Success -> {
                            mToast("上傳商品成功", "long")
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
                mToast("請上傳一張商品圖片")
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
                            mToast(result.error, "long")
                            _status.value = LoadApiStatus.ERROR
                        }
                    }
                }
            }
        }.await()
    }

    // when onclick addBtn add new list for user to set product data
    fun onClick(type: Int, positon: Int) {
        when (type) {
            0 -> addCapacityList.add(1, AddMenuItem.Detail(hashMapOf(), 0))
            1 -> addIceList.add(1, AddMenuItem.Detail(hashMapOf(), 1))
            2 -> addSugarList.add(1, AddMenuItem.Detail(hashMapOf(), 2))
            3 -> addOtherList.add(1, AddMenuItem.Detail(hashMapOf(), 3))
        }
        _addCapacityListLiveData.value = addCapacityList
        _addIceListLiveData.value = addIceList
        _addSugarListLiveData.value = addSugarList
        _addOtherListLiveData.value = addOtherList
    }


    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }
}