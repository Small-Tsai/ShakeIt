package com.tsai.shakeit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.FilterShop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.CurrentFragmentType
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.User
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ShakeItRepository) : ViewModel() {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    //use to display total comment number
    val commentSize = MutableLiveData<Int>()

    //use to display average rating
    val ratingAvg = MutableLiveData<Float>()

    //use to filter shop
    var shopFilterList = MutableLiveData<List<String>>()

    fun updateFilterShopList(list: List<String>) {
        viewModelScope.launch {
            repository.updateFilteredShop(FilterShop(list as ArrayList<String>))
        }
    }



}