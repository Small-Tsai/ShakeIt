package com.tsai.shakeit.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.util.MyContext

class HomeViewModel : ViewModel() {
    var binding: FragmentHomeBinding? = null
    val _isWalkOrRide = MutableLiveData<Boolean?>()
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

        if (isWalkOrRide.value == true && i>0) {
            i=0
            binding?.let {
                binding!!.walkFab.foreground =
                    MyContext.appContext?.getDrawable(R.drawable.walking_icon)
            }
            binding?.let {
                binding!!.rideFab.foreground = MyContext.appContext?.getDrawable(R.drawable.ride)
            }
        } else {
            i += 1
            binding?.let {
                binding!!.walkFab.foreground = MyContext.appContext?.getDrawable(R.drawable.ride)
            }
            binding?.let {
                binding!!.rideFab.foreground =
                    MyContext.appContext?.getDrawable(R.drawable.walking_icon)
            }
        }
        _isWalkOrRide.value = false

    }


}