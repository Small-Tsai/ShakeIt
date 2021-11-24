package com.tsai.shakeit.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.util.UserInfo
import com.tsai.shakeit.util.Util
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private var _favoriteItem = MutableLiveData<List<FavoriteItem>>()
    val favoriteItem: LiveData<List<FavoriteItem>>
        get() = _favoriteItem

    private var _myFavorite = MutableLiveData<List<Favorite>>()
    val myFavorite: LiveData<List<Favorite>>
        get() = _myFavorite

    private var _navToHome = MutableLiveData<Shop>()
    val navToHome: LiveData<Shop>
        get() = _navToHome

    val favoriteIsEmpty = MutableLiveData<Boolean?>().apply { value = true }

    init {
        getFavoriteData()
    }

    private fun getFavoriteData() {
        viewModelScope.launch {
            if (!Util.isInternetConnected()) {
                myToast(Util.getString(R.string.internet_not_connected))
            } else {
                repository.getFavorite(UserInfo.userId).collect {
                    (it as Result.Success).data.let { data -> _myFavorite.value = data }
                }
            }
        }
    }

    fun navToHome(shop: Shop) {
        _navToHome.value = shop
    }

    fun buildFavoriteList(favorite: List<Favorite>) {

        val favoriteList = mutableListOf<FavoriteItem>()
        val titleList = favorite.map { it.shop.name }.distinct()

        titleList.let { title ->
            title.forEach { name ->
                favorite.let {
                    favoriteList.add(FavoriteItem.ShopName(name))
                    favoriteList.add(FavoriteItem.ShopImg(favorite.filter { it.shop.name == name }
                        .map { it.shop }))
                }
            }
        }
        _favoriteItem.value = favoriteList
    }
}


