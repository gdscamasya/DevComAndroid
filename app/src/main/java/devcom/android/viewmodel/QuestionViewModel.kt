package devcom.android.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import devcom.android.logic.usecase.CheckLikedQuestions
import devcom.android.logic.usecase.LikedQuestion
import devcom.android.data.Question

class QuestionViewModel(
    private val likedQuestion: LikedQuestion,
    private val checkLikedQuestions: CheckLikedQuestions
) : ViewModel() {

    private val _isLikedQuestion = MutableLiveData<Boolean>()
    val isLikedQuestion: LiveData<Boolean>
        get() = _isLikedQuestion

    private val _isCheckLikedQuestions = MutableLiveData<Boolean>()
    val isCheckLikedQuestions: LiveData<Boolean>
        get() = _isCheckLikedQuestions


    fun likedQuestions(
        view: View,
        context: Context,
        questionList: ArrayList<Question>,
        position: Int,
        listName: String
    ) {
        likedQuestion.likedQuestions(
            view,
            context,
            questionList,
            position,
            listName,
            onSuccess = { _isLikedQuestion.value = true },
            onFailure = { _isLikedQuestion.value = false })
    }

     fun checkLikedQuestions(
         view: View,
         context: Context,
         questionList: ArrayList<Question>,
         listName: String
    ) {
        checkLikedQuestions.checkLiked(
            view,
            context,
            questionList,
            listName)
     }

}