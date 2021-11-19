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


    private val _comment = MutableLiveData<List<Comment>>()
    val comment: LiveData<List<Comment>>
        get() = _comment



    fun getComment() {
        viewModelScope.launch {
//            Logger.d("get comment from $shopId")
            when (val result = shopId?.let { repository.getComment(it) }) {
                is Result.Success -> {
                    _comment.value = result.data!!
                }
                is Result.Fail -> {
                    Logger.d("getComment Fail")
                }
            }
        }
    }

}