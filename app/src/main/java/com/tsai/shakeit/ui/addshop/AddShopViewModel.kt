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
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

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

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

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

    private suspend fun postImgUriToFireBase() {

        if (!Util.isInternetConnected()) {
            _status.value = LoadApiStatus.ERROR
            mToast(Util.getString(R.string.internet_not_connected))
        } else if (name.isEmpty() || branch.isEmpty() || address.isEmpty()) {
            mToast("店家名、分店名、店家地址不可空白喔！")
        } else {
            viewModelScope.async {
                _status.value = LoadApiStatus.LOADING
                shopImageUri.value?.let {
                    when (val result = withContext(Dispatchers.IO) {
                        repository.postImage(it)
                    }) {
                        is Result.Success -> {
                            _shopFireBaseImageUri.value = result.data!!
                        }
                        is Result.Fail -> {
                            mToast(result.error, "long")
                            _status.value = LoadApiStatus.ERROR
                        }
                    }
                }
            }
            viewModelScope.async {

                menuImageUri.value?.let {
                    when (val result = withContext(Dispatchers.IO) {
                        repository.postImage(it)
                    }) {
                        is Result.Success -> {
                            _menuFireBaseImageUri.value = result.data!!
                        }
                        is Result.Fail -> {
                            mToast(result.error, "long")
                            _status.value = LoadApiStatus.ERROR
                        }
                    }
                }
            }.await()
        }
    }

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
                            _status.value = LoadApiStatus.DONE
                        }
                        is Result.Fail -> {
                            mToast("發佈 ${shop.name}$branch 商店資訊失敗！")
                            _status.value = LoadApiStatus.ERROR
                        }
                    }
                }
            }
        }
    }

}

