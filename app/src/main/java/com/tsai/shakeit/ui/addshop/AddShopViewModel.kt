package com.tsai.shakeit.ui.addshop

import android.net.Uri
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class AddShopViewModel(private val repository: ShakeItRepository) : ViewModel() {

    val dateList = listOf<String>("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")

    private val _isDateOpen = MutableLiveData<Boolean>()
    val isDateOpen: LiveData<Boolean>
        get() = _isDateOpen

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    val shopImageUri = MutableLiveData<Uri>()

    val menuImageUri = MutableLiveData<Uri>()

    private val _navToHome = MutableLiveData<Boolean?>()
    val navToHome: LiveData<Boolean?>
        get() = _navToHome

    init {
        _isDateOpen.value = false
    }

    fun openDate() {
        _isDateOpen.value = _isDateOpen.value != true
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }

    private val _shopFireBaseImageUri = MutableLiveData<String>()
    private val shopFireBaseImageUri: LiveData<String>
        get() = _shopFireBaseImageUri

    private val _menuFireBaseImageUri = MutableLiveData<String>()
    private val menuFireBaseImageUri: LiveData<String>
        get() = _menuFireBaseImageUri

    private suspend fun postImgUriToFireBase() {
        viewModelScope.async {
            shopImageUri.value?.let {
                when (val result = repository.postImage(it)) {
                    is Result.Success -> {
                        mToast("上傳封面成功", "long")
                        _shopFireBaseImageUri.value = result.data!!
                    }
                    is Result.Fail -> {
                        mToast("上傳封面失敗", "long")
                    }
                }
            }
        }
        viewModelScope.async {
            menuImageUri.value?.let {
                when (val result = repository.postImage(it)) {
                    is Result.Success -> {
                        mToast("上傳菜單成功", "long")
                        _menuFireBaseImageUri.value = result.data!!
                    }
                    is Result.Fail -> {
                        mToast("上傳菜單失敗", "long")
                    }
                }
            }
        }.await()
    }


    var timeList: HashMap<String, String> = hashMapOf()
    var timeOpen = MutableLiveData<String>()
    var timeClose = MutableLiveData<String>()
    var adapterPostion = MutableLiveData<Int>()

    init {
        timeOpen.value = ""
        timeClose.value = ""
    }

    var name = ""
    var branch = ""
    var address = ""
    var tel = ""
    var lat = 0.0
    var lon = 0.0

    fun setTimeList(timeOpen: String, timeClose: String, position: Int) {
        when (position) {
            0 -> timeList["星期一"] = "$timeOpen-$timeClose"
            1 -> timeList["星期二"] = "$timeOpen-$timeClose"
            2 -> timeList["星期三"] = "$timeOpen-$timeClose"
            3 -> timeList["星期四"] = "$timeOpen-$timeClose"
            4 -> timeList["星期五"] = "$timeOpen-$timeClose"
            5 -> timeList["星期六"] = "$timeOpen-$timeClose"
            6 -> timeList["星期日"] = "$timeOpen-$timeClose"
        }
        Logger.d("$timeList")
    }

    fun postShopInfo() {
        viewModelScope.launch {
            postImgUriToFireBase()

            _shopFireBaseImageUri.value?.let {
                _menuFireBaseImageUri.value?.let { menu ->
                    val shop = Shop(
                        name = name,
                        branch = branch,
                        address = address,
                        tel = tel,
                        lat = lat,
                        lon = lon,
                        shop_Id = "",
                        shop_Img = it,
                        time = timeList,
                        menu_Img = menu,
                    )
                    when (val result = repository.postShopInfo(shop)) {
                        is Result.Success -> {
                            mToast("發佈 ${shop.name}$branch 商店資訊成功！")
                            _navToHome.value = true
                            _navToHome.value = false
                        }
                        is Result.Fail -> {
                            mToast("發佈 ${shop.name}$branch 商店資訊失敗！")
                        }
                    }
                }
            }
        }
    }

}

