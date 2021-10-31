package com.tsai.shakeit.ui.addshop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.source.ShakeItRepository
import kotlinx.coroutines.launch

class AddShopViewModel(repository: ShakeItRepository) : ViewModel() {

    val dateList = listOf<String>("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")

    private val _isDateOpen = MutableLiveData<Boolean>()
    val isDateOpen: LiveData<Boolean>
        get() = _isDateOpen

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack


    init {
        _isDateOpen.value = false
    }

    fun openDate() {
        _isDateOpen.value = _isDateOpen.value != true
    }

    fun popBack(){
        _popBack.value = true
        _popBack.value = null
    }

}