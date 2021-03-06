package com.tsai.shakeit.ui.order.sendcomment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tsai.shakeit.data.Comment
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.User
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo.userId
import com.tsai.shakeit.util.UserInfo.userImage
import com.tsai.shakeit.util.UserInfo.userName
import kotlinx.coroutines.launch

class CommentDialogViewModel(
    private val repository: ShakeItRepository,
    private val shopId: String?,
) : ViewModel() {

    private val _popBack = MutableLiveData<Boolean?>()
    val popBack: LiveData<Boolean?>
        get() = _popBack

    var editComment = ""
    var rating = 0f

    private val _comment = MutableLiveData<Comment>()
    val comment: LiveData<Comment>
        get() = _comment

    fun leave() {
        _popBack.value = true
        _popBack.value = null
    }

    private val _publishBtnClickable = MutableLiveData<Boolean>()
    val publishBtnClickable: LiveData<Boolean>
        get() = _publishBtnClickable

    init {
        _publishBtnClickable.value = true
    }

    fun postComment() {
        _comment.value = Comment(
            user = User(
                user_Id = userId,
                user_Name = userName,
                user_Image = userImage,
            ),
            comment = editComment,
            rating = rating,
            date = Timestamp.now()
        )
        Logger.d(_comment.value.toString())
    }

    fun send(comment: Comment) {
        _publishBtnClickable.value = false
        Logger.d("shop = $shopId")
        viewModelScope.launch {
            shopId?.let {
                when (val result = repository.postComment(shopId, comment)) {
                    is Result.Success -> {
                        _publishBtnClickable.value = true
                        leave()
                    }
                    is Result.Fail -> {
                        myToast("fail")
                        leave()
                    }
                    else -> {}
                }
            }
        }
    }
}
