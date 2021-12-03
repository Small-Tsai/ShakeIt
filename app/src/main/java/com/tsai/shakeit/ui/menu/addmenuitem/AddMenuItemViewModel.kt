package com.tsai.shakeit.ui.menu.addmenuitem

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.ui.menu.detail.OptionsType.*
import com.tsai.shakeit.util.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AddMenuItemViewModel(
    private val repository: ShakeItRepository,
    val shop: Shop?,
) : ViewModel() {

    // LiveData of capacityList for submit to Capacity recycleView
    private val _addCapacityListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value = mutableListOf()
    }
    val addCapacityListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addCapacityListLiveData

    // LiveData of capacityList for submit to Ice recycleView
    private val _addIceListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value = mutableListOf()
    }
    val addIceListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addIceListLiveData

    // LiveData of capacityList for submit to Sugar recycleView
    private val _addSugarListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value = mutableListOf()
    }
    val addSugarListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addSugarListLiveData

    // LiveData of capacityList for submit to Others recycleView
    private val _addOtherListLiveData = MutableLiveData<MutableList<AddMenuItem>>().apply {
        value = mutableListOf()
    }
    val addOthersListLiveData: LiveData<MutableList<AddMenuItem>>
        get() = _addOtherListLiveData

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    private val _navToMenu = MutableLiveData<Boolean?>()
    val navToMenu: LiveData<Boolean?>
        get() = _navToMenu

    // Use Two-way dataBinding to get text edit by user for option and product
    val optionName = MutableLiveData<String>()
    val optionPrice = MutableLiveData<String>()
    var productName = ""
    var productDescription = ""
    var productType = ""

    // LiveData of Map(optionName to optionPrice) map( "big" to 50 )
    private val _capacityListForPost = MutableLiveData<HashMap<String, Int>>()
    private val _iceListForPost = MutableLiveData<HashMap<String, Int>>()
    private val _sugarListForPost = MutableLiveData<HashMap<String, Int>>()
    private val _othersListForPost = MutableLiveData<HashMap<String, Int>>()

    // list of capacity, ice, sugar, others
    private var capacityList = mutableListOf<AddMenuItem>()
    private var iceList = mutableListOf<AddMenuItem>()
    private var sugarList = mutableListOf<AddMenuItem>()
    private var othersList = mutableListOf<AddMenuItem>()

    // initialize 4 LiveData
    init {
        _capacityListForPost.value = hashMapOf()
        _iceListForPost.value = hashMapOf()
        _sugarListForPost.value = hashMapOf()
        _othersListForPost.value = hashMapOf()
    }

    // Type 0->capacity , 1->ice , 2->sugar , 3->others
    // Initialize 4 list for submit to each recycleView when fragment onCreateView
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

    // Use to record adapterPosition which user select on recycleView
    private var currentSelectedPosition = -1
    fun recordCurrentSelectedPosition(position: Int) {
        currentSelectedPosition = position
    }

    // Use to record optionType which user select on recycleView
    // Type 0->capacity , 1->ice , 2->sugar , 3->others
    private var currentSelectedType = -1
    fun recordCurrentSelectedType(type: Int) {
        currentSelectedType = type
    }

    // Use to record options name edit by user
    private val capacityOptionsName = hashMapOf<Int, String?>()
    private val iceOptionsName = hashMapOf<Int, String?>()
    private val sugarOptionsName = hashMapOf<Int, String?>()
    private val othersOptionsName = hashMapOf<Int, String?>()


    /**
     * Set option name when observe optionName change
     *
     * Logic ->
     *
     * When user select editText record it's adapterPosition and it's type ->
     *
     * Due to Two-way dataBinding when text change trigger observe to do [setOptionName] function ->
     *
     * When type=0 -> it means user select on capacityOption ->
     *
     * capacityOptionsName[currentSelectedPosition] = [userImportContent] ->
     *
     * Get result ex. hashMap( 1 to "big" )
     */
    fun setOptionName(userImportContent: String) {
        when (currentSelectedType) {
            0 -> {
                if (userImportContent.isNotEmpty()) {
                    capacityOptionsName[currentSelectedPosition] = userImportContent
                } else {
                    capacityOptionsName[currentSelectedPosition] = null
                }
                Logger.d("capacityOptionContent = $capacityOptionsName")
            }
            1 -> {
                if (userImportContent.isNotEmpty()) {
                    iceOptionsName[currentSelectedPosition] = userImportContent
                } else {
                    iceOptionsName[currentSelectedPosition] = null
                }
                Logger.d("iceOptionContent = $iceOptionsName")
            }
            2 -> {
                if (userImportContent.isNotEmpty()) {
                    sugarOptionsName[currentSelectedPosition] = userImportContent
                } else {
                    sugarOptionsName[currentSelectedPosition] = null
                }
                Logger.d("sugarOptionContent = $sugarOptionsName")
            }
            3 -> {
                if (userImportContent.isNotEmpty()) {
                    othersOptionsName[currentSelectedPosition] = userImportContent
                } else {
                    othersOptionsName[currentSelectedPosition] = null
                }
                Logger.d("othersOptionContent = $othersOptionsName")
            }
        }
    }

    // Record editText price
    // Logic same as setOptionName function
    private val capacityOptionPrice = hashMapOf<Int, String>()
    private val othersOptionPrice = hashMapOf<Int, String>()
    fun setOptionPrice(price: String) {
        if (price.isNotEmpty()) {
            when (currentSelectedType) {
                0 -> capacityOptionPrice[currentSelectedPosition] = price
                3 -> othersOptionPrice[currentSelectedPosition] = price
            }
        }
    }

    /**
     * Use for implement common options
     *
     * clear old options -> set new options -> set new data for post -> refresh UI
     *
     * Get result ex. hashMap( 1 to "50" )
     */
    fun implementCommonOptions() {
        clearAllOption()
        setNewOptions()
        setNewPostData()
        refreshLiveData()
    }

    private fun clearAllOption() {
        currentSelectedPosition = -1
        capacityList.clear()
        iceList.clear()
        sugarList.clear()
        othersList.clear()
        capacityOptionsName.clear()
        iceOptionsName.clear()
        sugarOptionsName.clear()
        othersOptionsName.clear()
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
        capacityOptionsName[1] = "大"
        iceOptionsName[1] = "正常冰"
        iceOptionsName[2] = "少冰"
        iceOptionsName[3] = "微冰"
        iceOptionsName[4] = "去冰"
        sugarOptionsName[1] = "全糖"
        sugarOptionsName[2] = "7分糖"
        sugarOptionsName[3] = "5分糖"
        sugarOptionsName[4] = "3分糖"
        sugarOptionsName[5] = "1分糖"
        sugarOptionsName[6] = "無糖"
        othersOptionsName[1] = "珍珠"
        othersOptionsName[2] = "蘆薈"
        othersOptionsName[3] = "椰果"
        othersOptionPrice[1] = "10"
        othersOptionPrice[2] = "10"
        othersOptionPrice[3] = "10"
    }

    /**
     * merge content and price -> postProductImg await to get it's imgUri -> split shopName -> setProduct -> post productData to firebase
     *
     * Ex.
     *
     * [capacityOptionsName] = hashMap( 1 to "big" ) , [capacityOptionPrice] = hashMap( 1 to "50" )
     *
     * merge result -> [_capacityListForPost] = hashMap( "big" to "50" )
     *
     * shopName split -> ex. 可不可熟成紅茶 to `["可","可不","可不可"....]`
     *
     * setProduct -> put shopNameArray & imgUri & [_capacityListForPost]... to [Product] data class-> post product
     */
    @FlowPreview
    fun setProductDataThenPost() {
        when {
            capacityOptionsName.isNullOrEmpty() || capacityOptionPrice.isNullOrEmpty()
            -> myToast("請至少填寫一組容量與價格選項")
            iceOptionsName.isNullOrEmpty()
            -> myToast("請至少填寫一組冰量選項")
            sugarOptionsName.isNullOrEmpty()
            -> myToast("請至少填寫一組甜度選項")
            productName.isEmpty() || productType.isEmpty()
            -> myToast("商品名稱與類別不可留白喔")
            productImageUri.value == null
            -> myToast("請上傳一張商品圖片")
            else
            -> viewModelScope.launch {

                mergeOptionNameAndPrice()
                val productImg = repository.postImage(productImageUri.value!!)

                productImg
                    .flatMapConcat {
                        setProductData(shop!!, it)
                    }.flatMapConcat {
                        repository.postProduct(it)
                    }.collect { result ->
                        when (result) {
                            is Result.Success -> {
                                myToast("上傳商品成功", "long")
                                _status.value = LoadApiStatus.DONE
                                _navToMenu.value = true
                                _navToMenu.value = null
                            }
                            is Result.Fail -> _status.value = LoadApiStatus.ERROR
                            is Result.Error -> Logger.e(result.exception.toString())
                            else -> {}
                        }
                    }
            }
        }
    }

    private fun setProductData(
        shop: Shop,
        result: Result<String>,
    ): Flow<Product> {
        return flow {
            when (result) {
                is Result.Loading -> _status.value = LoadApiStatus.LOADING
                is Result.Success -> {

                    val shopNameArray = transFromShopNameToArray(shop)

                    // set product data
                    val product = Product(
                        name = productName.replace(" ", ""),
                        content = productDescription,
                        capacity = _capacityListForPost.value!!,
                        ice = _iceListForPost.value!!,
                        sugar = _sugarListForPost.value!!,
                        others = _othersListForPost.value!!,
                        shopId = shop.shop_Id,
                        shopAddress = shop.address,
                        shop_Name = shopNameArray,
                        branch = shop.branch.replace(" ", ""),
                        type = productType,
                        product_Img = result.data
                    )
                    Logger.d("product = $product")
                    emit(product)
                }

                is Result.Fail -> {
                    myToast(result.error)
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {}
            }
        }.catch { Logger.e("setProductData fail = ${it.message}") }
    }

    private fun transFromShopNameToArray(shop: Shop): ArrayList<String> {
        val shopNameArray = arrayListOf<String>()
        var shopName = ""
        for (i in shop.name.indices) {
            shopName += shop.name[i].toString()
            shopNameArray.add(shopName)
        }
        return shopNameArray
    }

    // Use to post product img
    val productImageUri = MutableLiveData<Uri>()

    private fun mergeOptionNameAndPrice() {
        capacityOptionsName.keys.forEach {
            if (!capacityOptionsName[it].isNullOrEmpty() && it > 0) {
                _capacityListForPost.value?.set(
                    capacityOptionsName[it] ?: "",
                    capacityOptionPrice[it]?.toInt() ?: 0
                )
            }
        }

        iceOptionsName.keys.forEach {
            if (!iceOptionsName[it].isNullOrEmpty()) {
                _iceListForPost.value?.set(
                    iceOptionsName[it] ?: "",
                    0
                )
            }
        }

        sugarOptionsName.keys.forEach {
            if (!sugarOptionsName[it].isNullOrEmpty()) {
                _sugarListForPost.value?.set(
                    sugarOptionsName[it] ?: "", 0
                )
            }
        }

        othersOptionsName.keys.forEach {
            if (!othersOptionsName[it].isNullOrEmpty())
                _othersListForPost.value?.set(
                    othersOptionsName[it] ?: "",
                    othersOptionPrice[it]?.toInt() ?: 0
                )
        }
    }

    // When onclick addBtn add new list for user to set product data
    fun addOption(type: Int) {
        when (type) {
            0 -> addNewOption(capacityList, CAPACITY.type)
            1 -> addNewOption(iceList, ICE.type)
            2 -> addNewOption(sugarList, SUGAR.type)
            3 -> addNewOption(othersList, OTHERS.type)
        }
        refreshLiveData()
    }

    /**
    When recycleView [addNewOption] it will re use same layout so if editText already has text on it
    it will duplicate that text on new option editText so before [addOption] have to setText to empty
    but setText to empty will trigger Two-way dataBinding and trigger [setOptionName] & price function
    it cause old data set to empty so before [addNewOption] set [currentSelectedType] and [currentSelectedPosition] to
    new [optionType] and set new position to [lastIndex] to prevent old data set to empty
     */
    private fun addNewOption(optionsList: MutableList<AddMenuItem>, optionType: Int) {
        currentSelectedType = optionType
        currentSelectedPosition = optionsList.lastIndex
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
