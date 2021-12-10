package com.tsai.shakeit.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.data.Shop

class SettingViewModel(private val list: Array<Shop>) : ViewModel() {

    private val _shopList = MutableLiveData<List<String>>().apply {
        value = list.map { it.name }.distinct()
    }
    val shopList: LiveData<List<String>>
        get() = _shopList

    private val _filteredShopName = MutableLiveData<String>()
    val filteredShopName: LiveData<String>
        get() = _filteredShopName

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    private val _isAllChecked = MutableLiveData<Boolean>()
    val isAllChecked: LiveData<Boolean>
        get() = _isAllChecked

    var filteredShopList = mutableListOf<String>()

    /**
     * If user filtered a shop then check is [filteredShopList] contains that shop or not
     * No -> add shop name to [filteredShopList] and assign value to [mainViewModel] localFilteredShopList
     * Yes -> remove shop name from [filteredShopList] and assign value to [mainViewModel] localFilteredShopList
     * When localFilteredShopList value change trigger observe to update firebaseFilteredShopList
     * and trigger [SettingFragment] observe to execute [checkIsFilteredShopListEmpty]
     */
    fun filterShop(shopName: String, mainViewModel: MainViewModel) {
        if (!filteredShopList.contains(shopName)) {
            filteredShopList.add(shopName)
            mainViewModel.localFilteredShopList.value = filteredShopList
        } else {
            filteredShopList.remove(shopName)
            mainViewModel.localFilteredShopList.value = filteredShopList
        }
    }

    fun checkIsFilteredShopListEmpty(remoteFilteredShopList: MutableList<String>) {
        _isAllChecked.value = remoteFilteredShopList.isEmpty()
    }

    fun getFilterShopName(shopName: String) {
        _filteredShopName.value = shopName
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }
}
