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

    private val _addCapacityListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value =
            mutableListOf()
    }
    val addCapacityListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addCapacityListLiveData

    private val _addIceListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value =
            mutableListOf()
    }
    val addIceListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addIceListLiveData

    private val _addSugarListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value =
            mutableListOf()
    }
    val addSugarListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addSugarListLiveData

    private val _addOtherListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value =
            mutableListOf()
    }
    val addOtherListLiveData: LiveData<MutableList<AddMenuItem>>
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

    private var addCapacityList = mutableListOf<AddMenuItem>()
    private var addIceList = mutableListOf<AddMenuItem>()
    private var addSugarList = mutableListOf<AddMenuItem>()
    private var addOtherList = mutableListOf<AddMenuItem>()

    init {
        _capacityList.value = hashMapOf()
        _iceList.value = hashMapOf()
        _sugarList.value = hashMapOf()
        _others.value = hashMapOf()
    }

    // type 0->capacity , 1->ice , 2->sugar , 3->other
    fun initSelectItem() {

        addCapacityList = mutableListOf(
            AddMenuItem.Title("容量選項"),
            AddMenuItem.Detail(hashMapOf(), 0),
            AddMenuItem.Button(0)
        )

        addIceList = mutableListOf(
            AddMenuItem.Title("冰量選項"),
            AddMenuItem.Detail(hashMapOf(), 1),
            AddMenuItem.Button(1)
        )

        addSugarList =
            mutableListOf(
                AddMenuItem.Title("甜度選項"),
                AddMenuItem.Detail(hashMapOf(), 2),
                AddMenuItem.Button(2)
            )

        addOtherList =
            mutableListOf(
                AddMenuItem.Title("加料選項"),
                AddMenuItem.Detail(hashMapOf(), 3),
                AddMenuItem.Button(3)
            )

        _addCapacityListLiveData.value = addCapacityList
        _addIceListLiveData.value = addIceList
        _addSugarListLiveData.value = addSugarList
        _addOtherListLiveData.value = addOtherList
    }


    var currentSelectedPostion = -1
    fun recordCurrentSelectedPosition(positon: Int) {
        Logger.d("cu = $positon")
        currentSelectedPostion = positon
    }

    var currentSelectedType = -1
    fun recordCurrentSelectedType(type: Int) {
        currentSelectedType = type
    }

    //record editText content
    private val userCapaContentList = hashMapOf<Int, String?>()
    private val userIceContentList = hashMapOf<Int, String?>()
    private val userSugarContentList = hashMapOf<Int, String?>()
    private val userOtherContentList = hashMapOf<Int, String?>()

    fun setListContent(contnet: String) {

        when (currentSelectedType) {
            0 -> {
                if (contnet.isNotEmpty()) {
                    userCapaContentList[currentSelectedPostion] = contnet
                } else {
                    userCapaContentList[currentSelectedPostion] = null
                }
                Logger.d("userCapaContentList = $userCapaContentList")
            }
            1 -> {
                if (contnet.isNotEmpty()) {
                    userIceContentList[currentSelectedPostion] = contnet
                } else {
                    userIceContentList[currentSelectedPostion] = null
                }
                Logger.d("userIceContentList = $userIceContentList")
            }
            2 -> {
                if (contnet.isNotEmpty()) {
                    userSugarContentList[currentSelectedPostion] = contnet
                } else {
                    userSugarContentList[currentSelectedPostion] = null
                }
                Logger.d("userSugarContentList = $userSugarContentList")
            }
            3 -> {
                if (contnet.isNotEmpty()) {
                    userOtherContentList[currentSelectedPostion] = contnet
                } else {
                    userOtherContentList[currentSelectedPostion] = null
                }
                Logger.d("userOtherContentList = $userOtherContentList")
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

    fun implementUsuallySet() {

        currentSelectedPostion = -1

        addCapacityList.clear()
        addIceList.clear()
        addSugarList.clear()
        addOtherList.clear()
        userCapaContentList.clear()
        userIceContentList.clear()
        userSugarContentList.clear()
        userOtherContentList.clear()

        addCapacityList = mutableListOf(
            AddMenuItem.Title("容量選項"),
            AddMenuItem.Detail(hashMapOf("大" to 0), 0),
            AddMenuItem.Button(0)
        )

        addIceList =
            mutableListOf(
                AddMenuItem.Title("冰量選項"),
                AddMenuItem.Detail(hashMapOf("正常冰" to 0), 1),
                AddMenuItem.Detail(hashMapOf("少冰" to 0), 1),
                AddMenuItem.Detail(hashMapOf("微冰" to 0), 1),
                AddMenuItem.Detail(hashMapOf("去冰" to 0), 1),
                AddMenuItem.Detail(hashMapOf("常溫" to 0), 1),
                AddMenuItem.Button(1)
            )

        addSugarList =
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

        addOtherList =
            mutableListOf(
                AddMenuItem.Title("加料選項"),
                AddMenuItem.Detail(hashMapOf("珍珠" to 10), 3),
                AddMenuItem.Detail(hashMapOf("蘆薈" to 10), 3),
                AddMenuItem.Detail(hashMapOf("椰果" to 10), 3),
                AddMenuItem.Button(3)
            )

        userCapaContentList[1] = "大"
        userIceContentList[1] = "正常冰"
        userIceContentList[2] = "少冰"
        userIceContentList[3] = "微冰"
        userIceContentList[4] = "去冰"
        userSugarContentList[1] = "全糖"
        userSugarContentList[2] = "7分糖"
        userSugarContentList[3] = "5分糖"
        userSugarContentList[4] = "3分糖"
        userSugarContentList[5] = "1分糖"
        userSugarContentList[6] = "無糖"
        userOtherContentList[1]="珍珠"
        userOtherContentList[2]="蘆薈"
        userOtherContentList[3]="椰果"
        userOtherPriceList[1]="10"
        userOtherPriceList[2]="10"
        userOtherPriceList[3]="10"

        _addCapacityListLiveData.value = addCapacityList
        _addIceListLiveData.value = addIceList
        _addSugarListLiveData.value = addSugarList
        _addOtherListLiveData.value = addOtherList
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
                    if (!userCapaContentList[it].isNullOrEmpty() && it > 0) {
                        _capacityList.value?.set(
                            userCapaContentList[it] ?: "",
                            userCapaPriceList[it]?.toInt() ?: 0
                        )
                    }
                }

                userIceContentList.keys.forEach {
                    if (!userIceContentList[it].isNullOrEmpty()) {
                        _iceList.value?.set(
                            userIceContentList[it] ?: "",
                            0
                        )
                    }
                }

                userSugarContentList.keys.forEach {
                    if (!userSugarContentList[it].isNullOrEmpty()) {
                        _sugarList.value?.set(
                            userSugarContentList[it] ?: "", 0
                        )
                    }
                }

                userOtherContentList.keys.forEach {
                    if (!userOtherContentList[it].isNullOrEmpty())
                        _others.value?.set(
                            userOtherContentList[it] ?: "",
                            userOtherPriceList[it]?.toInt() ?: 0
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
                    name = name.replace(" ", ""),
                    content = dercription,
                    capacity = _capacityList.value!!,
                    ice = _iceList.value!!,
                    sugar = _sugarList.value!!,
                    others = _others.value!!,
                    shopId = shop!!.shop_Id,
                    shopAddress = shop.address,
                    shop_Name = mArray,
                    branch = shop.branch.replace(" ", ""),
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
            0 -> {
                currentSelectedPostion = addCapacityList.lastIndex
                addCapacityList.add(addCapacityList.lastIndex, AddMenuItem.Detail(hashMapOf(), 0))
            }
            1 -> {
                currentSelectedPostion = addIceList.lastIndex
                addIceList.add(addIceList.lastIndex, AddMenuItem.Detail(hashMapOf(), 1))
            }
            2 -> {
                currentSelectedPostion = addSugarList.lastIndex
                addSugarList.add(addSugarList.lastIndex, AddMenuItem.Detail(hashMapOf(), 2))
            }
            3 -> {
                currentSelectedPostion = addOtherList.lastIndex
                addOtherList.add(addOtherList.lastIndex, AddMenuItem.Detail(hashMapOf(), 3))
            }
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