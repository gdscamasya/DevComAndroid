package devcom.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import devcom.android.logic.usecase.AnswerQuestionToSaveGlobal
import devcom.android.logic.usecase.AnswerQuestionToSavePersonal

class AnswerViewModelFactory(private val answerQuestionToSaveGlobal: AnswerQuestionToSaveGlobal, private val answerQuestionToSavePersonal: AnswerQuestionToSavePersonal) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AnswerViewModel::class.java)){
            return AnswerViewModel(answerQuestionToSaveGlobal,answerQuestionToSavePersonal) as T
        }
        throw IllegalArgumentException("ViewModel class not found!")
    }


}