package com.tsai.shakeit.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.databinding.FragmentHomeBinding

class HomeViewModel(repository: ShakeItRepository) : ViewModel() {
    var binding: FragmentHomeBinding? = null

    private val _isWalkOrRide = MutableLiveData<Boolean?>()
    val isWalkOrRide: LiveData<Boolean?>
        get() = _isWalkOrRide

    init {
        _isWalkOrRide.value = null
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
                binding!!.rideFab.foreground = ShakeItApplication.instance.getDrawable(R.drawable.ride)
            }
        } else {
            i += 1
            binding?.let {
                binding!!.walkFab.foreground = ShakeItApplication.instance.getDrawable(R.drawable.ride)
            }
            binding?.let {
                binding!!.rideFab.foreground =
                    ShakeItApplication.instance.getDrawable(R.drawable.walking_icon)
            }
        }
        _isWalkOrRide.value = false
    }


}