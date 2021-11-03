package com.tsai.shakeit.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.User
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: ShakeItRepository) : ViewModel() {

    fun uploadUser() {
        viewModelScope.launch {
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