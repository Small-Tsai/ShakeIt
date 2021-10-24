package com.tsai.shakeit.ui.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ui.home.TAG
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private var _favorite = MutableLiveData<List<Favorite>>()
    val favorite: LiveData<List<Favorite>>
        get() = _favorite


    init {
        getFavoriteData()
    }

    private fun getFavoriteData() {

        viewModelScope.launch {

            val favoriteList = mutableListOf<Favorite>()

            val fireBaseData = when (val result = repository.getFavorite()) {
                is Result.Success -> {
                    result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }

            val titleList = fireBaseData?.map { it.name }?.distinct()
            val imgList = fireBaseData?.map { it.shop_Img }


            Log.d(TAG, imgList.toString())
            titleList?.let { title ->
                title.forEach { name ->
                    favoriteList.add(Favorite.ShopName(name))
                    favoriteList.add(Favorite.ShopImg(fireBaseData.filter { it.name == name }))

                }
            }

            Log.d(TAG, favoriteList.toString())
            _favorite.value = favoriteList
        }
    }

}


sealed class Favorite {
    data class ShopName(val name: String) : Favorite()
    data class ShopImg(val img: List<Shop>) : Favorite()
}