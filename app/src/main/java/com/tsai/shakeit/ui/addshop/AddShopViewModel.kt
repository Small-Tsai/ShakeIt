package com.tsai.shakeit.ui.addshop

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.DayOfWeek
import com.google.android.libraries.places.api.model.Period
import com.tsai.shakeit.R
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

    private val _timeHashList = MutableLiveData<MutableList<HashMap<String, String>>>().apply {
        value = mutableListOf()
    }
    val timeHashList: LiveData<MutableList<HashMap<String, String>>>
        get() = _timeHashList


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

    private val _timeList =
        MutableLiveData<HashMap<String, String>>().apply { value = hashMapOf() }
    val timeList: LiveData<HashMap<String, String>>
        get() = _timeList

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
        } else if (shopImageUri.value == null || menuImageUri.value == null) {
            mToast("未上傳封面圖片或菜單圖片")
        } else {
            viewModelScope.async {
                shopImageUri.value?.let {
                    _status.value = LoadApiStatus.LOADING
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

    fun setTimeListByAutoComplete(periods: MutableList<Period>) {
        _timeHashList.value?.clear()
        periods.forEach {
            val openHour = it.open.time.hours
            val closeHour = it.close.time.hours
            val openMinutes = it.open.time.minutes
            val closeMinutes = it.close.time.minutes
            val openText = String.format("%02d:%02d", openHour, openMinutes)
            val closeText = String.format("%02d:%02d", closeHour, closeMinutes)
            _timeList.value?.let { list ->
                when (it.open.day) {
                    DayOfWeek.MONDAY -> {
                        list["星期一"] = "$openText - $closeText"
                        _timeHashList.value?.add(hashMapOf("星期一" to "$openText - $closeText"))
                    }
                    DayOfWeek.TUESDAY -> {
                        list["星期二"] = "$openText - $closeText"
                        _timeHashList.value?.add(hashMapOf("星期二" to "$openText - $closeText"))
                    }
                    DayOfWeek.WEDNESDAY -> {
                        list["星期三"] = "$openText - $closeText"
                        _timeHashList.value?.add(hashMapOf("星期三" to "$openText - $closeText"))
                    }
                    DayOfWeek.THURSDAY -> {
                        list["星期四"] = "$openText - $closeText"
                        _timeHashList.value?.add(hashMapOf("星期四" to "$openText - $closeText"))
                    }
                    DayOfWeek.FRIDAY -> {
                        list["星期五"] = "$openText - $closeText"

                        _timeHashList.value?.add(hashMapOf("星期五" to "$openText - $closeText"))
                    }
                    DayOfWeek.SATURDAY -> {
                        list["星期六"] = "$openText - $closeText"
                        _timeHashList.value?.add(hashMapOf("星期六" to "$openText - $closeText"))
                    }
                    DayOfWeek.SUNDAY -> {
                        list["星期日"] = "$openText - $closeText"
                        _timeHashList.value?.add(hashMapOf("星期日" to "$openText - $closeText"))
                    }
                }
                _timeHashList.value = _timeHashList.value
            }
        }
        Logger.d("timeList = ${_timeList.value}")
    }

    fun setTimeListWhenAutoCompleteFail() {
        _timeHashList.value?.addAll(
            listOf(
                hashMapOf("星期日" to ""),
                hashMapOf("星期一" to ""),
                hashMapOf("星期二" to ""),
                hashMapOf("星期三" to ""),
                hashMapOf("星期四" to ""),
                hashMapOf("星期五" to ""),
                hashMapOf("星期六" to "")
            )
        )
    }

    fun setTimeList(timeOpen: String, timeClose: String, position: Int) {
        _timeList.value?.let {
            when (position) {
                1 -> it["星期一"] = "$timeOpen-$timeClose"
                2 -> it["星期二"] = "$timeOpen-$timeClose"
                3 -> it["星期三"] = "$timeOpen-$timeClose"
                4 -> it["星期四"] = "$timeOpen-$timeClose"
                5 -> it["星期五"] = "$timeOpen-$timeClose"
                6 -> it["星期六"] = "$timeOpen-$timeClose"
                0 -> it["星期日"] = "$timeOpen-$timeClose"
            }
        }
        Logger.d("timeList = ${_timeList.value}")
    }

    fun postShopInfo() {
        viewModelScope.launch {

            postImgUriToFireBase()

            _shopFireBaseImageUri.value?.let {
                _menuFireBaseImageUri.value?.let { menu ->
                    val shop = Shop(
                        name = name.replace(" ", ""),
                        branch = branch,
                        address = address,
                        tel = tel,
                        lat = lat,
                        lon = lon,
                        shop_Id = "",
                        shop_Img = it,
                        time = _timeList.value,
                        menu_Img = menu,
                    )
                    when (val result = withContext(Dispatchers.IO) {
                        repository.postShopInfo(shop)
                    }) {
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

