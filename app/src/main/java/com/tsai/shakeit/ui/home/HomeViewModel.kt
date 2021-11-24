package com.tsai.shakeit.ui.home

import androidx.lifecycle.*
import com.google.android.libraries.maps.model.*
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.directionPlaceModel.Distance
import com.tsai.shakeit.data.directionPlaceModel.Duration
import com.tsai.shakeit.data.directionPlaceModel.Leg
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private val _navToMenu = MutableLiveData<Shop?>()
    val navToMenu: LiveData<Shop?>
        get() = _navToMenu

    private val _navToAddShop = MutableLiveData<Boolean?>()
    val navToAddShop: LiveData<Boolean?>
        get() = _navToAddShop

    private val _navToSetting = MutableLiveData<Boolean?>()
    val navToSetting: LiveData<Boolean?>
        get() = _navToSetting

    //LiveData of favoriteList
    private var _favorite = MutableLiveData<List<Favorite>>()
    val favorite: LiveData<List<Favorite>>
        get() = _favorite

    //LiveData for detect is now user select walking or driving
    private val _trafficMode =
        MutableLiveData<String>().apply { value = UserInfo.userCurrentSelectTraffic }
    val trafficMode: LiveData<String>
        get() = _trafficMode

    //LiveData for detect is now user using navigation or not
    private val _mapNavState = MutableLiveData<Boolean?>()
    val mapNavState: LiveData<Boolean?>
        get() = _mapNavState

    //LiveData for draw polyLine on map
    private val _options = MutableLiveData<PolylineOptions>()
    val options: LiveData<PolylineOptions>
        get() = _options

    //LiveData for detect googleMap camera movement
    private val _isMoveCamera = MutableLiveData<Boolean?>()
    val isMoveCamera: LiveData<Boolean?>
        get() = _isMoveCamera

    //LiveData for record user current searching product
    private val _userSearchingProduct = MutableLiveData<Product>()
    val userSearchingProduct: LiveData<Product>
        get() = _userSearchingProduct

    //Status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    //BottomSheet Status: The internal MutableLiveData that stores the status of the bottomSheet
    private val _bottomStatus = MutableLiveData<LoadApiStatus>()
    val bottomStatus: LiveData<LoadApiStatus>
        get() = _bottomStatus

    //LiveData of shopList
    private val _shopListLiveData = MutableLiveData<List<Shop>>()
    val shopListLiveData: LiveData<List<Shop>>
        get() = _shopListLiveData

    //Get shopName from shopLiveData
    val allShopName: LiveData<List<String>> = Transformations.map(shopListLiveData) { shop ->
        shop.map { it.name }.distinct()
    }

    //LiveData of product
    private val _allProduct = MutableLiveData<List<Product>>()
    val allProduct: LiveData<List<Product>>
        get() = _allProduct

    //Record current user selected shop snippet
    private val _snippet = MutableLiveData<String?>()

    //Record fragment type from home page
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    //LiveData of path distance
    val distanceLiveData = MutableLiveData<String>().apply { value = "" }

    //LiveData of path duration
    val trafficTimeLiveData = MutableLiveData<String>().apply { value = "" }

    //LiveData use for decide display shop opening time or not by DataBinding
    val isTimeDisplay = MutableLiveData<Boolean>().apply { value = false }

    //LiveData of userSetting traffic time
    val userSettingTime = MutableLiveData<String>().apply {
        value = UserInfo.userCurrentSettingTrafficTime
    }

    //LiveData for detect search focus
    val isSearchBarFocus = MutableLiveData<Boolean>().apply { value = false }

    //LiveData for record user selected shop
    val selectedShop = MutableLiveData<Shop>()

    //LiveData for detect selected shop is in my favorite or not
    val isInMyFavorite = MutableLiveData<Boolean>()

    //LiveData for detect getDirection done
    val getDirectionDone = MutableLiveData<Boolean>()

    //Init polyline option
    private var navOption = PolylineOptions()

    //Calculate distance from userSettingTime
    val distance: Double
        get() {
            var distance = 0.0
            if (!userSettingTime.value.isNullOrEmpty()) {
                when (_trafficMode.value) {
                    WALKING -> userSettingTime.value?.let {
                        distance = WALKING_SPEED_AVG * it.toInt()
                    }
                    DRIVING -> userSettingTime.value?.let {
                        distance = DRIVING_SPEED_AVG * it.toInt()
                    }
                }
            }
            return distance
        }

    //Decide polyLine style from traffic mode
    private val pattern: List<PatternItem>
        get() {
            val result = when (trafficMode.value) {
                WALKING -> listOf(Dot(), Gap(10f))
                else -> listOf(Dash(20f))
            }
            return result
        }

    init {
        getAllProduct()
        getMyFavorite()
    }

    fun clearSearchBarFocus() {
        isSearchBarFocus.value = false
    }

    private fun getAllProduct() {
        viewModelScope.launch {
            repository.getAllProduct().collect { result ->
                (result as Result.Success).also { _allProduct.value = result.data!! }
            }
        }
    }

    fun getUserSearchingProduct(product: Product) {
        _userSearchingProduct.value = product
    }

    fun getShopData(center: LatLng, type: String? = null) {
        viewModelScope.launch {
            repository.getAllShop(center, distance).collect { allShop ->
                when (allShop) {
                    is Result.Loading -> {
                        showIsSearchingToast()
                        if (type != "search") showLoading()
                    }
                    is Result.Success -> {
                        allShop.data.let {
                            _shopListLiveData.value = it
                            _status.value = LoadApiStatus.DONE
                        }
                    }
                    is Result.Fail -> {
                        _status.value = LoadApiStatus.ERROR
                        mToast(allShop.error)
                    }
                    is Result.Error -> allShop.exception.message?.let { Logger.e(it) }
                }
            }
        }
    }

    private fun showIsSearchingToast() {
        when (trafficMode.value) {
            WALKING -> {
                mToast("正在搜尋走路${userSettingTime.value}分鐘內的店家")
            }
            DRIVING -> {
                mToast("正在搜尋騎車${userSettingTime.value}分鐘內的店家")
            }
        }
    }

    private fun showLoading() {
        _status.value = LoadApiStatus.LOADING
    }

    //when click nav Done
    fun mapNavDone() {
        _mapNavState.value = false
        _mapNavState.value = null
    }

    //move camera
    fun moveCameraToCurrentLocation() {
        _isMoveCamera.value = true
        _isMoveCamera.value = null
    }

    // use to check has favorite or not
    private fun getMyFavorite() {
        viewModelScope.launch {
            repository.getFavorite(UserInfo.userId).collect {
                (it as Result.Success).data.let { data -> _favorite.value = data }
                checkHasFavorite()
            }
        }
    }

    var mShopId: String? = null
    fun checkHasFavorite() {
        isInMyFavorite.value = _favorite.value?.map { it.shop.shop_Id }?.contains(mShopId)
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
            repository.deleteFavorite(shopId).collect {
                if (it is Result.Success) {
                    mToast("已移除此收藏")
                }
            }
        }
    }

    //upload favorite
    fun postMyFavorite(favorite: Favorite) {
        viewModelScope.launch {
            repository.postFavorite(favorite).collect {
                when (it) {
                    is Result.Success -> {
                        checkHasFavorite()
                        mToast("已將 ${favorite.shop.name + favorite.shop.branch} 加入收藏")
                    }
                    else -> {
                        Logger.e((it as Result.Fail).error)
                    }
                }
            }
        }
    }

    //get selected shop
    fun getSelectedShopSnippet(markerSnippet: String) {
        mShopId = markerSnippet
        _snippet.value = markerSnippet
        selectedShop.value = shopListLiveData.value?.first { it.shop_Id == markerSnippet }
    }

    //bottomSheet shop open time
    fun timeDisplayOrNot() {
        isTimeDisplay.value = isTimeDisplay.value == false
    }

    fun selectWalk() {
        UserInfo.userCurrentSelectTraffic = WALKING
        _trafficMode.value = UserInfo.userCurrentSelectTraffic
    }

    fun selectDriving() {
        UserInfo.userCurrentSelectTraffic = DRIVING
        _trafficMode.value = UserInfo.userCurrentSelectTraffic
    }

    fun doNothing() {}

    fun startDrawPolyLine() {
        _options.value = navOption
    }

    //Call DirectionApi
    fun getDirection(url: String) {
        viewModelScope.launch {

            navOption = PolylineOptions().apply {
                width(20f)
                color(Util.getColor(R.color.blue))
                geodesic(true)
                visible(true)
            }

            repository.getDirection(url).collect { result ->
                when (result) {
                    is Result.Loading -> _bottomStatus.value = LoadApiStatus.LOADING
                    is Result.Success -> {
                        val directions = result.data
                        val route = directions.routes[0]
                        val leg = route.legs[0]
                        val stepList: MutableList<LatLng> = ArrayList()
                        updateBottomSheetUI(leg.distance, leg.duration)
                        setPolyLineData(leg, stepList)
                        getDirectionDone.value = true
                        _bottomStatus.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        mToast(result.error)
                    }
                    is Result.Error -> Logger.e(result.exception.toString())
                }
            }
        }
    }

    private fun setPolyLineData(
        leg: Leg,
        stepList: MutableList<LatLng>
    ): PolylineOptions? {

        navOption.jointType(JointType.ROUND)
        navOption.pattern(pattern)

        for (stepModel in leg.steps) {
            val decodedList = Util.decode(stepModel.polyline.points)
            for (latLng in decodedList) {
                stepList.add(LatLng(latLng.latitude, latLng.longitude))
            }
        }
        return navOption.addAll(stepList)
    }

    private fun updateBottomSheetUI(distance: Distance, duration: Duration) {
        distanceLiveData.value = distance.text
        trafficTimeLiveData.value = duration.text
    }
}


