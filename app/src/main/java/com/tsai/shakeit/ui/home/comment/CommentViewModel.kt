package com.tsai.shakeit.ui.home.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Comment
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import kotlinx.coroutines.launch

class CommentViewModel(private val repository: ShakeItRepository, private val shopId: String?) :
    ViewModel() {

    private val _commentList = MutableLiveData<List<Comment>>()
    val commentList: LiveData<List<Comment>>
        get() = _commentList

    fun getComment() {
        viewModelScope.launch {
            when (val result = shopId?.let { repository.getComment(it) }) {
                is Result.Success -> {
                    _commentList.value = result.data!!
                }
                is Result.Fail -> {
                    Logger.d(result.error)
                }
                else -> {}
            }
        }
    }

}