package com.tsai.shakeit.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.User

class SettingViewModel(private val repository: ShakeItRepository, private val list: Array<Shop>) :
    ViewModel() {

    private val _shopList = MutableLiveData<List<String>>().apply {
        value = list.map { it.name }
    }
    val shopList: LiveData<List<String>>
        get() = _shopList

    var filteredList = mutableListOf<String>()

    fun filterShop(shopName: String, mainViewModel: MainViewModel) {
        Logger.d(filteredList.toString())
        if (!filteredList.contains(shopName)) {

            filteredList.add(shopName)
            mainViewModel.shopFilterList.value = filteredList

        } else {
            filteredList.remove(shopName)
            mainViewModel.shopFilterList.value = filteredList
        }
    }

    var dbFilterShopList = MutableLiveData<List<String>>()
    fun getFilterList() {
        dbFilterShopList = repository.getFilteredShopList(User.userId)
    }

}