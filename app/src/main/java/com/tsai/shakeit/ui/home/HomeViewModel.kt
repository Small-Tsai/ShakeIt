package com.tsai.shakeit.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.Toast
import androidx.lifecycle.*
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.util.Logger
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

    private var _Favorite = MutableLiveData<List<Shop>>()
    val Favorite: LiveData<List<Shop>>
        get() = _Favorite

    private val _snippet = MutableLiveData<String?>()
    val snippet: LiveData<String?>
        get() = _snippet

    private val _navToAddShop = MutableLiveData<Boolean?>()
    val navToAddShop: LiveData<Boolean?>
        get() = _navToAddShop

    val timeDisplay = MutableLiveData<Boolean>()

    val _selectedShop = MutableLiveData<Shop>()

    init {
        timeDisplay.value = false
        _isWalkOrRide.value = null
        viewModelScope.launch {
            when (val result = repository.getAllShop()) {
                is Result.Success -> _shopLiveData.value = result.data!!
                is Result.Fail -> Log.d(TAG, "getShop Failed")
            }
        }
        getMyFavorite()
    }

    // use to check has favorite or not
    fun getMyFavorite() {
        viewModelScope.launch {
            _Favorite = repository.getFavorite()
        }
    }

    var mShopId: String? = null
    fun checkHasFavorite(shopId: String) {
        _isInserted.value = _Favorite.value?.map { it.shop_Id }?.contains(mShopId)
    }

    fun deleteFavorite(shopId: String) {

        viewModelScope.launch {
            when (val result = repository.deleteFavorite(shopId)) {
                is Result.Success -> {
                    checkHasFavorite(shopId)
                }
            }
        }
    }

    fun postMyFavorite(shop: Shop) {

        viewModelScope.launch {
            when (val result =
                repository.postFavorite(shop)) {
                is Result.Success -> {
                    checkHasFavorite(shop.shop_Id)
                    Toast.makeText(
                        ShakeItApplication.instance,
                        "已將 ${shop.name + shop.branch} 加入收藏",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Fail -> {
                    Toast.makeText(
                        ShakeItApplication.instance,
                        "加入收藏失敗",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Error -> {
                    Log.d(TAG, "postFavorite Error")
                }
            }
        }
    }


    fun getSelectedShopSnippet(markerSnippet: String) {
        mShopId = markerSnippet
        Log.d(TAG, "getSnippet $markerSnippet")
        _snippet.value = markerSnippet
        _selectedShop.value = shopLiveData.value?.first { it.shop_Id == markerSnippet }
    }

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
            binding?.let {
                binding!!.walkFab.foreground =
                    ShakeItApplication.instance.getDrawable(R.drawable.walking_icon)
            }
            binding?.let {
                binding!!.rideFab.foreground =
                    ShakeItApplication.instance.getDrawable(R.drawable.ride)
            }
        } else {
            i += 1
            binding?.let {
                binding!!.walkFab.foreground =
                    ShakeItApplication.instance.getDrawable(R.drawable.ride)
            }
            binding?.let {
                binding!!.rideFab.foreground =
                    ShakeItApplication.instance.getDrawable(R.drawable.walking_icon)
            }
        }
        _isWalkOrRide.value = false
    }

    fun onAddButtonClicked(b: Boolean) {
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