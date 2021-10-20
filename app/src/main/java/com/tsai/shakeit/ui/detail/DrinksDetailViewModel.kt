package com.tsai.shakeit.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.Product

class DrinksDetailViewModel(val data: Product) : ViewModel() {

    private val _product = MutableLiveData<List<DrinksDetail>>()
    val product: LiveData<List<DrinksDetail>>
        get() = _product

    private val _qty = MutableLiveData<Int>()
    val qty: LiveData<Int>
        get() = _qty

    private val _popBack = MutableLiveData<Boolean?>()
    val popback: LiveData<Boolean?>
        get() = _popBack

    init {
        _qty.value = 1
        filterList()
    }

    fun plus() {
        _qty.value = _qty.value?.plus(1)
    }

    fun minus() {
        _qty.value = _qty.value?.minus(1)
    }

    fun filterList() {
        val detailList = mutableListOf<DrinksDetail>()
        detailList.add(DrinksDetail.DetailTitle("容量"))
        data.capacity.forEach { detailList.add(DrinksDetail.DetailContent(it)) }
        detailList.add(DrinksDetail.DetailTitle("冰量"))
        data.ice.forEach { detailList.add(DrinksDetail.DetailContent(it)) }
        detailList.add(DrinksDetail.DetailTitle("甜度"))
        data.sugar.forEach { detailList.add(DrinksDetail.DetailContent(it)) }
        detailList.add(DrinksDetail.DetailTitle("加料"))
        data.others.forEach { detailList.add(DrinksDetail.DetailContent(it)) }
        _product.value = detailList
    }

    fun popBack() {
        _popBack.value = true
        _popBack.value = null
    }
}