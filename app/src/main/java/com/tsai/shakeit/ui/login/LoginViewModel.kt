package com.tsai.shakeit.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.User
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private val _navToOrder = MutableLiveData<Boolean?>()
    val navToOrder: LiveData<Boolean?>
        get() = _navToOrder

    suspend fun uploadUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                Logger.d("uploadUser")
                repository.postUserInfo(
                    User(
                        user_Id = UserInfo.userId,
                        user_Name = UserInfo.userName,
                        user_Image = UserInfo.userImage
                    )
                )
            }
        }
    }

    fun joinToOrder(orderId: String) {
        viewModelScope.launch {
            Logger.d("joinOrder")
            when (val result = withContext(Dispatchers.IO) {
                repository.joinToOrder(orderId)
            }) {
                is Result.Success -> {
                    _navToOrder.value = true
                    _navToOrder.value = null
                }
            }
        }

    }
}