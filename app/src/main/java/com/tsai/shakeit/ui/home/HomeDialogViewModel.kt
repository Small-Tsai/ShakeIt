package com.tsai.shakeit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeDialogViewModel : ViewModel() {

    private val _hasNavToMenu = MutableLiveData<Boolean>()
    val hasNavToMenu: LiveData<Boolean>
        get() = _hasNavToMenu

    fun navToMenu() {
        _hasNavToMenu.value = true
    }

    fun navToMenuDone(){
        _hasNavToMenu.value = null
    }
}