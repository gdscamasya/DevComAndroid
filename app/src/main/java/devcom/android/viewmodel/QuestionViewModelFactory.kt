package devcom.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import devcom.android.logic.usecase.CheckLikedQuestions
import devcom.android.logic.usecase.LikedQuestion

class QuestionViewModelFactory(private val likedQuestion: LikedQuestion,private val checkLikedQuestions: CheckLikedQuestions):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(QuestionViewModel::class.java)){
            return QuestionViewModel(likedQuestion,checkLikedQuestions) as T
        }
        throw IllegalArgumentException("ViewModel class not found!")
    }
}