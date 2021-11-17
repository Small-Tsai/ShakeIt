package com.tsai.shakeit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.util.*
import kotlinx.coroutines.*


class HomeViewModel(private val repository: ShakeItRepository) : ViewModel() {
    var binding: FragmentHomeBinding? = null

    private val _navToMenu = MutableLiveData<Shop?>()
    val navToMenu: LiveData<Shop?>
        get() = _navToMenu

    private val _isInserted = MutableLiveData<Boolean>()
    val isInserted: LiveData<Boolean>
        get() = _isInserted

    private val _shopLiveData = MutableLiveData<List<Shop>>()
    val shopLiveData: LiveData<List<Shop>>
        get() = _shopLiveData

    private var _favorite = MutableLiveData<List<Favorite>>()
    val favorite: LiveData<List<Favorite>>
        get() = _favorite

    private val _snippet = MutableLiveData<String?>()
    val snippet: LiveData<String?>
        get() = _snippet

    private val _navToAddShop = MutableLiveData<Boolean?>()
    val navToAddShop: LiveData<Boolean?>
        get() = _navToAddShop

    private val _navToSetting = MutableLiveData<Boolean?>()
    val navToSetting: LiveData<Boolean?>
        get() = _navToSetting

    private val _isfilterShopBtnClickable = MutableLiveData<Boolean>()
    val isfilterShopClickable: LiveData<Boolean>
        get() = _isfilterShopBtnClickable

    private val _mode =
        MutableLiveData<String>().apply { value = UserInfo.userCurrentSelectTraffic }
    val mode: LiveData<String>
        get() = _mode

    private val _mapNavState = MutableLiveData<Boolean?>()
    val mapNavState: LiveData<Boolean?>
        get() = _mapNavState

    private val _options = MutableLiveData<PolylineOptions>()
    val options: LiveData<PolylineOptions>
        get() = _options

    private val _moveCamera = MutableLiveData<Boolean?>()
    val moveCamera: LiveData<Boolean?>
        get() = _moveCamera

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    // BottomSheet Status: The internal MutableLiveData that stores the status of the bottomSheet
    private val _bottomStatus = MutableLiveData<LoadApiStatus>()
    val bottomStatus: LiveData<LoadApiStatus>
        get() = _bottomStatus

    //LiveData of product
    private val _allProduct = MutableLiveData<List<Product>>()
    val allproduct: LiveData<List<Product>>
        get() = _allProduct

    //record fragment type from home page
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()


    val distanceLiveData = MutableLiveData<String>().apply { value = "" }
    val trafficTimeLiveData = MutableLiveData<String>().apply { value = "" }
    val timeDisplay = MutableLiveData<Boolean>().apply { value = false }
    val userSettingTime =
        MutableLiveData<String>().apply { value = UserInfo.userCurrentSettingTrafficTime }
    val selectedShop = MutableLiveData<Shop>()
    var distance: Double = 0.0

    init {
        getProduct()
        getMyFavorite()
    }

    //getProduct
    private fun getProduct() {

        viewModelScope.launch {

            when (val result = withContext(Dispatchers.IO) {
                repository.getAllProduct()
            }) {
                is Result.Success -> {
                    result.data.let {
                        _allProduct.value = it
                    }
                }
            }
        }
    }

    //getShop
    fun getShopData(center: LatLng, type: String? = null) {
        viewModelScope.launch {

            if (type == "search") {
                // do search animation
            } else {
                loading()
            }

            when (mode.value) {
                WALKING -> userSettingTime.value?.let { distance = WALKING_SPEED_AVG * it.toInt() }
                DRIVING -> userSettingTime.value?.let { distance = DRIVING_SPEED_AVG * it.toInt() }
            }

            _isfilterShopBtnClickable.value = false

            when (val result = withContext(Dispatchers.IO) {
                repository.getAllShop(center, distance)
            }) {
                is Result.Success -> {
                    result.data.let {
                        _shopLiveData.value = it
                        _isfilterShopBtnClickable.value = true
                        _status.value = LoadApiStatus.DONE
                    }
                }
                is Result.Fail -> {
                    mToast(result.error, "long")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun loading() {
        _status.value = LoadApiStatus.LOADING
    }

    //when click nav Done
    fun mapNavDone() {
        _mapNavState.value = false
        _mapNavState.value = null
    }

    //move camera
    fun moveCameraToCurrentLocation() {
        _moveCamera.value = true
        _moveCamera.value = null
    }

    // use to check has favorite or not
    private fun getMyFavorite() {
        viewModelScope.launch {
            _favorite = withContext(viewModelScope.coroutineContext) {
                withContext(Dispatchers.Main) {
                    repository.getFavorite(UserInfo.userId)
                }
            }
        }
    }

    var mShopId: String? = null
    fun checkHasFavorite() {
        _isInserted.value = _favorite.value?.map { it.shop.shop_Id }?.contains(mShopId)
    }

    //nav to setting page
    fun navToSetting() {
        _navToSetting.value = true
        _navToSetting.value = null
    }

    //nav to menu page
    fun navToMenu(shop: Shop) {
        _navToMenu.value = shop
        _navToMenu.value = null
    }

    //nav to addShop page
    fun navToAddShop() {
        _navToAddShop.value = true
        _navToAddShop.value = null
    }

    //delete favorite
    fun deleteFavorite(shopId: String) {
        viewModelScope.launch {
            when (repository.deleteFavorite(shopId)) {
                is Result.Success -> checkHasFavorite()
                else -> Logger.d("delete favorite fail ")
            }
        }
    }

    //upload favorite
    fun postMyFavorite(favorite: Favorite) {
        viewModelScope.launch {
            when (val result = withContext(Dispatchers.IO) {
                repository.postFavorite(favorite)
            }) {
                is Result.Success -> {
                    checkHasFavorite()
                    mToast("已將 ${favorite.shop.name + favorite.shop.branch} 加入收藏")
                }
                is Result.Fail -> mToast("加入收藏失敗")
                is Result.Error -> Logger.e(result.exception.toString())
            }
        }
    }

    //get selected shop
    fun getSelectedShopSnippet(markerSnippet: String) {
        mShopId = markerSnippet
        _snippet.value = markerSnippet
        selectedShop.value = shopLiveData.value?.first { it.shop_Id == markerSnippet }
    }

    //bottomSheet shop open time
    fun displayOrNot() {
        timeDisplay.value = timeDisplay.value == false
    }

    fun selectWalk() {

        UserInfo.userCurrentSelectTraffic = WALKING
        _mode.value = UserInfo.userCurrentSelectTraffic
    }

    fun selectDriving() {
        UserInfo.userCurrentSelectTraffic = DRIVING
        _mode.value = UserInfo.userCurrentSelectTraffic

    }

    fun doNothing() {}

    private val currentPositon = MutableLiveData<LatLng>()
    val getDirectionDone = MutableLiveData<Boolean>()
    private var navOption = PolylineOptions()

    fun drawPolyLine() {
        _options.value = navOption
    }

    fun getCurrentPosition(currentPositon: LatLng) {
        this.currentPositon.value = currentPositon
    }

    fun getDirection(url: String, mode: String) {
        viewModelScope.launch {

            _bottomStatus.value = LoadApiStatus.LOADING

            navOption = PolylineOptions().apply {
                width(20f)
                color(Util.getColor(R.color.blue))
                geodesic(true)
                visible(true)
            }

            when (val result = withContext(Dispatchers.IO) {
                repository.getDirection(url)
            }) {
                is Result.Success -> {
                    async {
                        val directions = result.data
                        val route = directions.routes[0]
                        val leg = route.legs[0]
                        val distance = leg.distance
                        val duration = leg.duration
                        val stepList: MutableList<LatLng> = ArrayList()
                        val pattern: List<PatternItem>

                        distanceLiveData.value = distance.text
                        trafficTimeLiveData.value = duration.text

                        if (mode == WALKING) {
                            pattern = listOf(Dot(), Gap(10f))
                            navOption.jointType(JointType.ROUND)
                        } else {
                            pattern = listOf(Dash(20f))
                        }

                        navOption.pattern(pattern)

                        for (stepModel in leg.steps) {
                            val decodedList = decode(stepModel.polyline.points)
                            for (latLng in decodedList) {
                                stepList.add(LatLng(latLng.latitude, latLng.longitude))
                            }
                        }
                        navOption.addAll(stepList)
                    }.await()

                    getDirectionDone.value = true
                    _bottomStatus.value = LoadApiStatus.DONE
                }

                is Result.Fail -> {
                    binding?.root?.let {
                        Snackbar.make(
                            it, result.error,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}


private fun decode(points: String): List<LatLng> {
    val len = points.length
    val path: MutableList<LatLng> = java.util.ArrayList(len / 2)
    var index = 0
    var lat = 0
    var lng = 0
    while (index < len) {
        var result = 1
        var shift = 0
        var b: Int
        do {
            b = points[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
        result = 1
        shift = 0
        do {
            b = points[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
        path.add(LatLng(lat * 1e-5, lng * 1e-5))
    }
    return path
}
