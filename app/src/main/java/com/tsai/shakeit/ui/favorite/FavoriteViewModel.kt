package com.tsai.shakeit.ui.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ui.home.TAG
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private var _favorite = MutableLiveData<List<Favorite>>()
    val favorite: LiveData<List<Favorite>>
        get() = _favorite

    private var _shop = MutableLiveData<List<Shop>>()
    val shop: LiveData<List<Shop>>
        get() = _shop

    init {
        getFavoriteData()
    }

    private fun getFavoriteData() {
        viewModelScope.launch {
            _shop = repository.getFavorite()
        }
    }

    fun buildFavoriteList(shop:List<Shop>){

        val favoriteList = mutableListOf<Favorite>()
        val titleList = shop.map { it.name }.distinct()

        titleList.let { title ->
            title.forEach { name ->
                shop.let {
                    favoriteList.add(Favorite.ShopName(name))
                    favoriteList.add(Favorite.ShopImg(shop.filter { it.name == name }))
                }
            }
        }
        _favorite.value = favoriteList
    }
}


