package com.tsai.shakeit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.CurrentFragmentType

class MainViewModel(repository: ShakeItRepository) : ViewModel() {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()


}