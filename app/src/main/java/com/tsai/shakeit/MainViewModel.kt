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

    // use to display total comment number
    val commentCount = MutableLiveData<Int>()

    // use to display average rating
    val ratingAvg = MutableLiveData<Float>()

    // Record current filtered shop and update to firebase
    var localShopFilteredList = MutableLiveData<List<String>>()

    // Record current selected shop
    val selectedShop = MutableLiveData<Shop>()

    // liveData for observe firebaseFilteredShopList
    var firebaseFilteredShopList = MutableLiveData<List<String>>()

    // use to update filterShopList on FireBase
    fun updateFilterShopList(list: List<String>) {
        viewModelScope.launch {
            repository.updateFilteredShop(FilterShop(list as ArrayList<String>))
        }
    }

    // get FireBaseShopFilteredList on FireBase
    fun getFireBaseFilteredShopList() {
        if (firebaseFilteredShopList.value.isNullOrEmpty()) {
            firebaseFilteredShopList = repository.getUserFilteredShopList(UserInfo.userId)
        }
    }
}
