package com.tsai.shakeit.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.maps.model.Marker
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ShakeItRepository) : ViewModel() {
    var binding: FragmentHomeBinding? = null

    private val _isWalkOrRide = MutableLiveData<Boolean?>()
    val isWalkOrRide: LiveData<Boolean?>
        get() = _isWalkOrRide

    private val _shopData = MutableLiveData<List<Shop>>()
    val shopData: LiveData<List<Shop>>
        get() = _shopData

    init {
        _isWalkOrRide.value = null
        viewModelScope.launch {
            when (val result = repository.getAllShop()) {
                is Result.Success -> _shopData.value = result.data
                is Result.Fail -> Log.d(TAG, "getShop Failed")
            }
        }
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

    private val _snippet = MutableLiveData<String?>()
    val snippet: LiveData<String?>
        get() = _snippet

    fun navToDetail(markerSnippet: String) {
        _snippet.value = markerSnippet
    }

    fun navDone(){
        _snippet.value = null
    }
}