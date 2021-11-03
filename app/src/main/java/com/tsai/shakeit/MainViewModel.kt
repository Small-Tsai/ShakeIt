package com.tsai.shakeit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.FilterShop
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.CurrentFragmentType
import com.tsai.shakeit.util.UserInfo
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

    //use to navToHome from Favorite Page and move camera
    val selectedFavorite = MutableLiveData<Shop>()

    //use to update filterShopList on FireBase
    fun updateFilterShopList(list: List<String>) {
        viewModelScope.launch {
            repository.updateFilteredShop(FilterShop(list as ArrayList<String>))
        }
    }

    //get filterShopList on FireBase
    var dbFilterShopList = MutableLiveData<List<String>>()
    fun getFilterList() {
        dbFilterShopList = repository.getFilteredShopList(UserInfo.userId)
    }

}