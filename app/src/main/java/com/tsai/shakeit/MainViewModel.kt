package com.tsai.shakeit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.CurrentFragmentType

class MainViewModel(repository: ShakeItRepository) : ViewModel() {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    //use to display total comment number
    val commentSize = MutableLiveData<Int>()

    //use to display average rating
    val ratingAvg = MutableLiveData<Float>()

}