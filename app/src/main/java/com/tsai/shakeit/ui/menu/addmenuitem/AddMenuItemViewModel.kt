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
import com.tsai.shakeit.databinding.AddMenuItemRowBinding
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.util.Logger
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class AddMenuItemViewModel(
    private val repository: ShakeItRepository,
    private val shop: Shop?
) : ViewModel() {

    lateinit var binding: AddMenuItemRowBinding

    private val _capacityList = MutableLiveData<HashMap<String, Int>>()
    val capacityList: LiveData<HashMap<String, Int>>
        get() = _capacityList

    private val _iceList = MutableLiveData<HashMap<String, Int>>()
    val iceList: LiveData<HashMap<String, Int>>
        get() = _iceList

    private val _sugarList = MutableLiveData<HashMap<String, Int>>()
    val sugarList: LiveData<HashMap<String, Int>>
        get() = _sugarList

    private val _others = MutableLiveData<HashMap<String, Int>>()
    val others: LiveData<HashMap<String, Int>>
        get() = _others

    private val _addMenuItemList = MutableLiveData<List<AddMenuItem>>()
    val addMenuItemList: LiveData<List<AddMenuItem>>
        get() = _addMenuItemList

    private val _productFireBaseImageUri = MutableLiveData<String>()
    private val productFireBaseImageUri: LiveData<String>
        get() = _productFireBaseImageUri

    val content = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    var name = ""
    var dercription = ""
    var type = ""

    var addMenuList = mutableListOf<AddMenuItem>()

    init {
        _capacityList.value = hashMapOf()
        _iceList.value = hashMapOf()
        _sugarList.value = hashMapOf()
        _others.value = hashMapOf()
        initSelectItem()
    }

    var firstTime = 0
    var capaEndPosition = 0
    var iceEndPosition = 0
    var sugarEndPosition = 0
    var otherEndPosition = 0

    // type 0->capacity , 1->ice , 2->sugar , 3->other
    fun initSelectItem() {
        if (firstTime == 0) {
            addMenuList.add(AddMenuItem.Title("容量選項"))
            addMenuList.add(AddMenuItem.Detail(hashMapOf()))
            addMenuList.add(AddMenuItem.Button(0))
            addMenuList.add(AddMenuItem.Title("冰量選項"))
//            addMenuList.add(AddMenuItem.Detail(hashMapOf()))
            addMenuList.add(AddMenuItem.Button(1))
            addMenuList.add(AddMenuItem.Title("甜度選項"))
//            addMenuList.add(AddMenuItem.Detail(hashMapOf()))
            addMenuList.add(AddMenuItem.Button(2))
            addMenuList.add(AddMenuItem.Title("加料選項"))
//            addMenuList.add(AddMenuItem.Detail(hashMapOf()))
            addMenuList.add(AddMenuItem.Button(3))
            _addMenuItemList.value = addMenuList
            firstTime = 1
        }
    }

    var currentSelectedPostion = -1
    fun recordCurrentSelectedPostion(positon: Int) {
        Logger.d("$positon")
        currentSelectedPostion = positon
    }

    private val userCapaContentList = hashMapOf<Int, String>()
    private val userIceContentList = hashMapOf<Int, String>()
    private val userSugarContentList = hashMapOf<Int, String>()
    private val userOtherContentList = hashMapOf<Int, String>()

    var capaMax = 0
    var iceMax = 0
    var sugarMax = 0

    fun setListContent(contnet: String) {

        capaMax = 1 + capaEndPosition
        iceMax = 4 + iceEndPosition + capaEndPosition
        sugarMax = 7 + iceEndPosition + capaEndPosition + sugarEndPosition

        if (currentSelectedPostion <= capaMax) {
            userCapaContentList[currentSelectedPostion] = contnet
            Logger.d("caContent =$userCapaContentList")
        } else if (currentSelectedPostion in (capaMax + 1)..iceMax) {
            userIceContentList[currentSelectedPostion] = contnet
            Logger.d("iceContent =$userIceContentList")
        } else if (currentSelectedPostion <= sugarMax && currentSelectedPostion > 1 + iceMax) {
            userSugarContentList[currentSelectedPostion] = contnet
            Logger.d("sugarContent =$userSugarContentList")
        } else {
            userOtherContentList[currentSelectedPostion] = contnet
            Logger.d("otherContent =$userOtherContentList")
        }
    }

    private val userCapaPriceList = hashMapOf<Int, String>()
    private val userOtherPriceList = hashMapOf<Int, String>()
    fun setListPrice(price: String) {

        capaMax = 1 + capaEndPosition
        iceMax = 4 + iceEndPosition + capaEndPosition
        sugarMax = 7 + iceEndPosition + capaEndPosition + sugarEndPosition


        if (currentSelectedPostion <= capaMax) {
            userCapaPriceList[currentSelectedPostion] = price
            Logger.d("capa =$userCapaPriceList")
        } else if (currentSelectedPostion in (capaMax + 1)..iceMax) {
            // TODO("if need price")
        } else if (currentSelectedPostion <= sugarMax && currentSelectedPostion > 1 + iceMax) {
            // TODO("if need price")
        } else {
            userOtherPriceList[currentSelectedPostion] = price
            Logger.d("other = $userOtherPriceList")
        }
    }

    val productImageUri = MutableLiveData<Uri>()
    private suspend fun postImgUriToFireBase() {
        viewModelScope.async {
            productImageUri.value?.let {
                when (val result = repository.postImage(it)) {
                    is Result.Success -> {
                        _productFireBaseImageUri.value = result.data!!
                        mToast("上傳商品成功", "long")
                    }
                    is Result.Fail -> {
                        mToast("上傳封面失敗", "long")
                    }
                }
            }
        }.await()
    }


    fun mergeAllList() {
//        Logger.d("1= $userCapaContentList")
//        Logger.d("2= $userIceContentList")
//        Logger.d("3= $userSugarContentList")
//        Logger.d("4= $userOtherContentList")
//        Logger.d("5= $userCapaPriceList")
//        Logger.d("6= $userOtherPriceList")

        viewModelScope.launch {
//            postImgUriToFireBase()

            userCapaContentList.keys.forEach {
                _capacityList.value?.set(
                    userCapaContentList[it] ?: "",
                    userCapaPriceList[it]?.toInt() ?: 0
                )
            }
            userIceContentList.keys.forEach {
                _iceList.value?.set(userIceContentList[it] ?: "", 0)
            }
            userSugarContentList.keys.forEach {
                _sugarList.value?.set(userSugarContentList[it] ?: "", 0)
            }
            userOtherContentList.keys.forEach {
                _others.value?.set(
                    userOtherContentList[it] ?: "",
                    userOtherPriceList[it]?.toInt() ?: 0
                )
            }

            Logger.d("resultCa = ${_capacityList.value}")
            Logger.d("resultIc = ${_iceList.value}")
            Logger.d("resultSu = ${_sugarList.value}")
            Logger.d("resultOt = ${_others.value}")


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
//            repository.postProduct(product)
        }
    }

    fun onClick(type: Int, positon: Int) {
        when (type) {
            0 -> {
                addMenuList.add(1, AddMenuItem.Detail(hashMapOf()))
                capaEndPosition += 1
            }
            1 -> {
                addMenuList.add(
                    4 + capaEndPosition,
                    AddMenuItem.Detail(hashMapOf())
                )
                iceEndPosition += 1
                Logger.d("iceEnd = $iceEndPosition")
            }
            2 -> {
                addMenuList.add(
                    6 + iceEndPosition + capaEndPosition,
                    AddMenuItem.Detail(hashMapOf())
                )
                sugarEndPosition += 1
                Logger.d("sugarEnd = $sugarEndPosition")
            }
            3 -> {
                addMenuList.add(
                    8 + sugarEndPosition + iceEndPosition + capaEndPosition,
                    AddMenuItem.Detail(hashMapOf())
                )
                otherEndPosition += 1
            }
        }
        _addMenuItemList.value = addMenuList
    }
}