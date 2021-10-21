package com.tsai.shakeit.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.ui.home.TAG

class DrinksDetailViewModel(val data: Product) : ViewModel() {

    private val _product = MutableLiveData<List<DrinksDetail>>()
    val product: LiveData<List<DrinksDetail>>
        get() = _product

    private val _qty = MutableLiveData<Int>()
    val qty: LiveData<Int>
        get() = _qty

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    private val _refresh = MutableLiveData<Boolean?>()
    val refresh: LiveData<Boolean?>
        get() = _refresh

    var selectedPositionList = mutableListOf<Int>()
    var mContentList: MutableMap<String, ArrayList<String>> = mutableMapOf()


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

    private fun filterList() {
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

    var resultSize = 0
    private val capacitySize = data.capacity.size
    private val iceSize = data.ice.size
    private val sugarSize = data.sugar.size
    private val othersSize = data.others.size
    private val rangeCapacity = 1..capacitySize
    private val rangeCapacityToIce = selectRange(capacitySize, iceSize)
    private val rangeIceToSugar = selectRange(resultSize, sugarSize)
    private val rangeSugarToOthers = selectRange(resultSize, othersSize)
    private var firstClick = 0

    fun doSelect(position: Int, content: String) {

        if (firstClick == 0) {
            selectedPositionList.add(position); firstClick += 1
        }

        when (position) {
            in rangeCapacity -> {
                refactorPositionList(position, selectedPositionList, rangeCapacity, content)
            }
            in rangeCapacityToIce -> {
                refactorPositionList(position, selectedPositionList, rangeCapacityToIce, content)
            }
            in rangeIceToSugar -> {
                refactorPositionList(position, selectedPositionList, rangeIceToSugar, content)
            }
            in rangeSugarToOthers -> {
                refactorListInOthersRange(position, content)
            }
        }
        _refresh.value = true
        _refresh.value = null
    }

    private fun refactorListInOthersRange(i: Int, content: String) {

        if (selectedPositionList.contains(i)) {
            mContentList["others"]?.remove(content)
            selectedPositionList.remove(i)
            Log.d(TAG, mContentList.toString())
        } else {
            selectedPositionList.add(i)

            if (mContentList["others"] == null) {
                mContentList["others"] = arrayListOf(content)
            } else {
                mContentList["others"]?.add(content)
            }
            Log.d(TAG, mContentList.toString())
        }
    }

    private fun refactorPositionList(
        i: Int,
        list: MutableList<Int>,
        range: IntRange,
        content: String
    ) {
        list.add(i)
        val mList = list.filter { it !in range } as MutableList<Int>
        mList.add(i)
        selectedPositionList = mList

        when (range) {
            rangeCapacity -> mContentList["capacity"] = arrayListOf(content)
            rangeCapacityToIce -> mContentList["ice"] = arrayListOf(content)
            rangeIceToSugar -> mContentList["sugar"] = arrayListOf(content)
        }
        Log.d(TAG, mContentList.toString())
    }

    private fun selectRange(lastSize: Int, nextSize: Int): IntRange {
        resultSize = lastSize + 1 + nextSize
        return lastSize + 2..resultSize
    }
}