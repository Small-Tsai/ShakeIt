package com.tsai.shakeit.ui.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.Menu
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.ui.home.TAG

const val REDTEA = "紅茶"
const val GREENTEA = "綠茶"
const val WULONG = "烏龍茶"

class MenuViewModel : ViewModel() {

    private val _productList = MutableLiveData<List<Menu>>()
    val productList: LiveData<List<Menu>>
        get() = _productList

    private val mockData =
        listOf(
            Product("熟成紅茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", REDTEA),
            Product("熟成綠茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", GREENTEA),
            Product("熟成紅茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", REDTEA),
            Product("熟成紅茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", REDTEA),
            Product("熟成烏龍茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", WULONG),
            Product("熟成紅茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", REDTEA),
            Product("熟成烏龍茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", WULONG),
            Product("熟成綠茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", GREENTEA),
            Product("熟成烏龍茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", WULONG),
            Product("熟成綠茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", GREENTEA),
            Product("熟成烏龍茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", WULONG),
            Product("熟成烏龍茶", "好香", arrayListOf(), arrayListOf(), arrayListOf(), 35, arrayListOf(), "可不可熟成紅茶", WULONG),
        )

    init {
        filterMyList()
    }

    private fun filterMyList() {

        val mList = mutableListOf<Menu>()
        val titleList = mutableListOf<String>()

        for (i in mockData.indices) { titleList.add(mockData[i].type) }

        val newTitleList = titleList.distinct()

        for(i in newTitleList.indices) {
            val list = mockData.filter { it.type == newTitleList[i] }
            mList.add(Menu.Title(newTitleList[i]))
            for (x in list.indices) { mList.add(Menu.MenuProduct(list[x])) }
        }
        _productList.value = mList
    }
}

