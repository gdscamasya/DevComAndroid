package devcom.android.viewmodel

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import devcom.android.logic.usecase.CheckLikedQuestions
import devcom.android.logic.usecase.LikedQuestion
import devcom.android.users.Question

class QuestionViewModel(
    private val likedQuestion: LikedQuestion,
    private val checkLikedQuestions: CheckLikedQuestions
) : ViewModel() {

    private val _isLikedQuestion = MutableLiveData<Boolean>()
    val isLikedQuestion: LiveData<Boolean>
        get() = _isLikedQuestion




    fun likedQuestions(
        view: View,
        context: Context,
        questionList: ArrayList<Question>,
        position: Int
    ) {
        likedQuestion.likedQuestions(
            view,
            context,
            questionList,
            position,
            onSuccess = { _isLikedQuestion.value = true },
            onFailure = { _isLikedQuestion.value = false })


    }

    suspend fun checkLikedQuestions(
        view: View,
        context: Context,
        likedQuestion: ArrayList<String?>,
        questionList: ArrayList<Question>,
        likeIndexQuestionList: ArrayList<Int?>
    ) {
        checkLikedQuestions.checkLiked(view,context, likedQuestion, questionList, likeIndexQuestionList)

    }


}