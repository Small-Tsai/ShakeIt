package com.tsai.shakeit.ui.home

import androidx.lifecycle.*
import com.google.android.libraries.maps.model.*
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.app.DRIVING
import com.tsai.shakeit.app.DRIVING_SPEED_AVG
import com.tsai.shakeit.app.WALKING
import com.tsai.shakeit.app.WALKING_SPEED_AVG
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.directionPlaceModel.Distance
import com.tsai.shakeit.data.directionPlaceModel.Duration
import com.tsai.shakeit.data.directionPlaceModel.Leg
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.util.CurrentFragmentType
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import com.tsai.shakeit.util.Util
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

    // LiveData of favoriteList
    private var _favoriteList = MutableLiveData<List<Favorite>>()
    val favoriteList: LiveData<List<Favorite>>
        get() = _favoriteList

    // LiveData for detect is now user select walking or driving
    private val _trafficMode =
        MutableLiveData<String>().apply { value = UserInfo.userCurrentSelectTrafficMode }
    val trafficMode: LiveData<String>
        get() = _trafficMode

    // LiveData for detect is now user using navigation or not
    private val _mapNavState = MutableLiveData<Boolean?>()
    val mapNavState: LiveData<Boolean?>
        get() = _mapNavState

    // LiveData for draw polyLine on map
    private val _options = MutableLiveData<PolylineOptions>()
    val options: LiveData<PolylineOptions>
        get() = _options

    // LiveData for detect googleMap camera movement
    private val _isMoveCamera = MutableLiveData<Boolean?>()
    val isMoveCamera: LiveData<Boolean?>
        get() = _isMoveCamera

    // LiveData for record user current searching product
    private val _userSearchingProduct = MutableLiveData<Product>()
    val userSearchingProduct: LiveData<Product>
        get() = _userSearchingProduct

    // Status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    // BottomSheet Status: The internal MutableLiveData that stores the status of the bottomSheet
    private val _bottomStatus = MutableLiveData<LoadApiStatus>()
    val bottomStatus: LiveData<LoadApiStatus>
        get() = _bottomStatus

    // LiveData of shopList
    private val _shopListLiveData = MutableLiveData<List<Shop>>()
    val shopListLiveData: LiveData<List<Shop>>
        get() = _shopListLiveData

    // Get shopName from shopLiveData
    val allShopName: LiveData<List<String>> = Transformations.map(shopListLiveData) { shop ->
        shop.map { it.name }.distinct()
    }

    // LiveData of product
    val allProduct: LiveData<List<Product>> = liveData {
        repository.getAllProduct().collect { result ->
            emit((result as Result.Success).data.sortedBy { it.type })
        }
    }

    // Record current user selected shop snippet
    private val _selectedShopId = MutableLiveData<String?>()
    val selectedShopId: LiveData<String?> = _selectedShopId

    // Record fragment type from home page
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    // LiveData of path distance
    val distanceLiveData = MutableLiveData<String>().apply { value = "" }

    // LiveData of path duration
    val trafficTimeLiveData = MutableLiveData<String>().apply { value = "" }

    // LiveData use for decide display shop opening time or not by DataBinding
    val isTimeDisplay = MutableLiveData<Boolean>().apply { value = false }

    // LiveData of userSetting traffic time
    val userSettingTime = MutableLiveData<String>().apply {
        value = UserInfo.userCurrentSettingTrafficTime
    }

    // LiveData for record user selected shop
    val selectedShop = MutableLiveData<Shop>()

    // LiveData for detect selected shop is in my favorite or not
    val isInMyFavorite = MutableLiveData<Boolean>()

    // LiveData for detect getDirection done
    val getDirectionDone = MutableLiveData<Boolean>()

    /**
     * Calculate [distance] from [userSettingTime] -> distance = averageSpeed * userSettingTime
     */
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

    // Decide polyLine style from traffic mode, walk->dot, driving->dash
    private val pattern: List<PatternItem>
        get() {
            val result = when (trafficMode.value) {
                WALKING -> listOf(Dot(), Gap(10f))
                else -> listOf(Dash(20f))
            }
            return result
        }

    init {
        if (ShakeItApplication.instance.isLiveDataDesign()) {
            getMyFavorite(UserInfo.userId)
        }
    }


    /**
     * LiveData for detect search focus
     * FilterBtn will transform to closeSearchBarBtn By dataBinding it
     */
    val isSearchBarFocus = MutableLiveData<Boolean>().apply { value = false }

    // CloseSearchBarBtn onClick -> clear searchBar focus , null prevent navigation agin
    fun clearSearchBarFocus() {
        isSearchBarFocus.value = false
    }

    // FilterBtn onClick -> navToSetting page , null prevent navigation again
    fun navToSetting() {
        _navToSetting.value = true
        _navToSetting.value = null
    }

    fun getUserSearchingProduct(product: Product) {
        _userSearchingProduct.value = product
    }

    /**
     * If [distance] between shopLocation and userLocation smaller then [distance] then get it's data
     * Distance between shopLocation and [userLocation] calculate by firebase using GeoLocation
     */
    fun getShopData(userLocation: LatLng, type: String? = null) {
        viewModelScope.launch {
            repository.getAllShop(userLocation, distance).collect { allShop ->
                when (allShop) {
                    is Result.Loading -> {
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
                        myToast(allShop.error)
                    }
                    is Result.Error -> allShop.exception.message?.let { Logger.e(it) }
                }
            }
        }
    }

    private fun showLoading() {
        _status.value = LoadApiStatus.LOADING
    }

    fun mapNavDone() {
        _mapNavState.value = false
        _mapNavState.value = null
    }

    fun moveCameraToCurrentLocation() {
        _isMoveCamera.value = true
        _isMoveCamera.value = null
    }

    // Use to check has favorite or not
    fun getMyFavorite(userId: String) {
        viewModelScope.launch {
            repository.getFavorite(userId).collect {
                (it as Result.Success).data.let { data -> _favoriteList.value = data }
                checkHasFavorite()
            }
        }
    }

    /**
     * After user select a shop check is [favoriteList] contains [selectedShopId]
     * Yes -> trigger dataBinding to show red heart on bottomSheet, No -> show empty heart
     */
    fun checkHasFavorite() {
        isInMyFavorite.value =
            _favoriteList.value?.map { it.shop.shop_Id }?.contains(_selectedShopId.value)
    }

    // Nav to menu page
    fun navToMenu(shop: Shop) {
        _navToMenu.value = shop
        _navToMenu.value = null
    }

    // Nav to addShop page
    fun navToAddShop() {
        _navToAddShop.value = true
        _navToAddShop.value = null
    }

    // Delete favorite by shopId
    fun deleteFavorite(shopId: String) {
        viewModelScope.launch {
            repository.deleteFavorite(shopId).collect {
                if (it is Result.Success) {
                    myToast("已移除此收藏")
                }
            }
        }
    }

    // Post favorite
    fun postMyFavorite(favorite: Favorite) {
        viewModelScope.launch {
            repository.postFavorite(favorite).collect {
                when (it) {
                    is Result.Success -> {
                        checkHasFavorite()
                        myToast("已將 ${favorite.shop.name + favorite.shop.branch} 加入收藏")
                    }
                    else -> {
                        Logger.e((it as Result.Fail).error)
                    }
                }
            }
        }
    }

    // Get selectedShopId by it's snippet
    fun getSelectedShopSnippet(markerSnippet: String) {
        _selectedShopId.value = markerSnippet
        _shopListLiveData.value?.let { filterShopListByShopId(markerSnippet, it) }
    }

    // After get user selectedShop shopId filter shopDataList by it
    fun filterShopListByShopId(markerSnippet: String, shopList: List<Shop>) {
        selectedShop.value = shopList.first { it.shop_Id == markerSnippet }
    }

    // BottomSheet shop open time display or not by dataBinding it to layout
    fun timeDisplayOrNot() {
        isTimeDisplay.value = isTimeDisplay.value == false
    }

    fun selectWalk() {
        UserInfo.userCurrentSelectTrafficMode = WALKING
        _trafficMode.value = UserInfo.userCurrentSelectTrafficMode
    }

    fun selectDriving() {
        UserInfo.userCurrentSelectTrafficMode = DRIVING
        _trafficMode.value = UserInfo.userCurrentSelectTrafficMode
    }

    // When user click bottomSheet constraintLayout do nothing to prevent bottomSheet close
    fun doNothing() {}

    fun startDrawPolyLine(navOptions: PolylineOptions) {
        _options.value = navOptions
    }

    // Call DirectionApi
    fun getDirection(url: String, navOptions: PolylineOptions) {
        viewModelScope.launch {

            repository.getDirection(url).collect { result ->
                when (result) {
                    is Result.Loading -> _bottomStatus.value = LoadApiStatus.LOADING
                    is Result.Success -> {
                        val directions = result.data
                        val route = directions.routes[0]
                        val leg = route.legs[0]
                        val stepList: MutableList<LatLng> = ArrayList()
                        updateBottomSheetUI(leg.distance, leg.duration)
                        setPolyLineData(leg, stepList, navOptions)
                        getDirectionDone.value = true
                        _bottomStatus.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        myToast(result.error)
                    }
                    is Result.Error -> Logger.e(result.exception.toString())
                }
            }
        }
    }

    private fun setPolyLineData(
        leg: Leg,
        stepList: MutableList<LatLng>,
        navOptions: PolylineOptions,
    ) {

        navOptions.jointType(JointType.ROUND)
        navOptions.pattern(pattern)

        for (stepModel in leg.steps) {
            val decodedList = Util.decode(stepModel.polyline.points)
            for (latLng in decodedList) {
                stepList.add(LatLng(latLng.latitude, latLng.longitude))
            }
        }
        navOptions.addAll(stepList)
    }

    private fun updateBottomSheetUI(distance: Distance, duration: Duration) {
        distanceLiveData.value = distance.text
        trafficTimeLiveData.value = duration.text
    }
}
