package com.tsai.shakeit.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.Util

class SettingViewModel(private val repository: ShakeItRepository, private val list: Array<Shop>) :
    ViewModel() {

    private val _shopList = MutableLiveData<List<String>>().apply {
        value = list.map { it.name }
    }
    val shopList: LiveData<List<String>>
        get() = _shopList

    var filteredList = mutableListOf<String>()

    var isAllChecked = MutableLiveData<Boolean>()

    fun filterShop(shopName: String, mainViewModel: MainViewModel) {

        Logger.d("isAllChecked = ${isAllChecked.value}")

        if (!filteredList.contains(shopName)) {
            Logger.d("addShop")
            filteredList.add(shopName)
            mainViewModel.shopFilterList.value = filteredList

        } else {
            Logger.d("removeShop")
            filteredList.remove(shopName)
            mainViewModel.shopFilterList.value = filteredList
        }
    }

    val doCheck = MutableLiveData<String>()

    fun doCheck(shopName: String) {
        doCheck.value = shopName
    }

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }

}