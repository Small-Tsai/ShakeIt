package com.tsai.shakeit.ui.home

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ShakeItRepository) : ViewModel() {
    var binding: FragmentHomeBinding? = null

    private val _hasNavToMenu = MutableLiveData<Shop?>()
    val hasNavToMenu: LiveData<Shop?>
        get() = _hasNavToMenu

    private val _isInserted = MutableLiveData<Boolean>()
    val isInserted: LiveData<Boolean>
        get() = _isInserted

    private val _isWalkOrRide = MutableLiveData<Boolean?>()
    val isWalkOrRide: LiveData<Boolean?>
        get() = _isWalkOrRide

    private val _shopLiveData = MutableLiveData<List<Shop>>()
    val shopLiveData: LiveData<List<Shop>>
        get() = _shopLiveData

    private var _Favorite = MutableLiveData<List<Favorite>>()
    val Favorite: LiveData<List<Favorite>>
        get() = _Favorite

    private val _snippet = MutableLiveData<String?>()
    val snippet: LiveData<String?>
        get() = _snippet

    private val _navToAddShop = MutableLiveData<Boolean?>()
    val navToAddShop: LiveData<Boolean?>
        get() = _navToAddShop

    private val _navToSetting = MutableLiveData<Boolean?>()
    val navToSetting: LiveData<Boolean?>
        get() = _navToSetting

    val timeDisplay = MutableLiveData<Boolean>()

    val selectedShop = MutableLiveData<Shop>()

    private val _isfilterShopBtnClickable = MutableLiveData<Boolean>()
    val isfilterShopClickable: LiveData<Boolean>
        get() = _isfilterShopBtnClickable

    init {
        timeDisplay.value = false
        _isWalkOrRide.value = null
        getShopData()
        getMyFavorite()
    }

    //獲取店家資料
    private fun getShopData() {
        viewModelScope.launch {

            _isfilterShopBtnClickable.value = false

            when (val result = repository.getAllShop()) {
                is Result.Success -> {
                    _shopLiveData.value = result.data!!
                    _isfilterShopBtnClickable.value = true
                }
                is Result.Fail -> mToast("獲取店家資料異常")
            }
        }
    }


    fun navToSetting() {
        _navToSetting.value = true
        _navToSetting.value = null
    }

    // use to check has favorite or not
    private fun getMyFavorite() {
        viewModelScope.launch {
            _Favorite = repository.getFavorite(UserInfo.userId)
        }
    }

    var mShopId: String? = null
    fun checkHasFavorite() {
        _isInserted.value = _Favorite.value?.map { it.shop.shop_Id }?.contains(mShopId)
    }

    //刪除收藏
    fun deleteFavorite(shopId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteFavorite(shopId)) {
                is Result.Success -> checkHasFavorite()
            }
        }
    }

    //上傳收藏
    fun postMyFavorite(favorite: Favorite) {
        viewModelScope.launch {
            when (val result = repository.postFavorite(favorite)) {
                is Result.Success -> {
                    checkHasFavorite()
                    mToast("已將 ${favorite.shop.name + favorite.shop.branch} 加入收藏")
                }
                is Result.Fail -> mToast("加入收藏失敗")
                is Result.Error -> Logger.d("postFavorite Error")
            }
        }
    }

    //用Snippet獲得選取的店家
    fun getSelectedShopSnippet(markerSnippet: String) {
        mShopId = markerSnippet
        _snippet.value = markerSnippet
        selectedShop.value = shopLiveData.value?.first { it.shop_Id == markerSnippet }
    }

    //bottomSheet 營業時間顯示與否
    fun displayOrNot() {
        timeDisplay.value = timeDisplay.value == false
    }

    fun navToMenu(shop: Shop) {
        _hasNavToMenu.value = shop
        _hasNavToMenu.value = null
    }

    fun navToAddShop() {
        _navToAddShop.value = true
        _navToAddShop.value = null
    }

    fun isWalk() {
        _isWalkOrRide.value = _isWalkOrRide.value == null
    }

    fun isNull() {
        _isWalkOrRide.value = null
    }

    var i = 0
    @SuppressLint("UseCompatLoadingForDrawables")
    fun isRide() {

        if (isWalkOrRide.value == true && i > 0) {
            i = 0

            binding?.walkFab?.let {
                it.foreground = ShakeItApplication.instance.getDrawable(R.drawable.walking_icon)
            }
            binding?.rideFab?.let {
                it.foreground = ShakeItApplication.instance.getDrawable(R.drawable.ride)
            }

        } else {
            i += 1

            binding?.walkFab?.let {
                it.foreground = ShakeItApplication.instance.getDrawable(R.drawable.ride)
            }

            binding?.rideFab?.let {
                it.foreground = ShakeItApplication.instance.getDrawable(R.drawable.walking_icon)
            }

        }
        _isWalkOrRide.value = false
    }

    fun onWalkOrRideBtnClicked(b: Boolean) {
        setVisibility(b)
        setAnimation(b)
    }

    private fun setAnimation(b: Boolean) {
        val fromTop =
            AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.from_top_anim)
        val toBottom =
            AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.to_bottom_anim)
        if (b) {
            binding?.rideFab?.startAnimation(toBottom)
        } else {
            binding?.rideFab?.startAnimation(fromTop)
        }
    }

    private fun setVisibility(b: Boolean) {
        if (b) binding?.rideFab?.visibility = View.VISIBLE
        else binding?.rideFab?.visibility = View.GONE
    }
}