package devcom.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import devcom.android.logic.usecase.AskQuestionToPersonalSave
import devcom.android.logic.usecase.AskQuestionsToSaveGlobal

class FormViewModelFactory(private val askQuestionToPersonalSave: AskQuestionToPersonalSave, private val askQuestionsToSaveGlobal: AskQuestionsToSaveGlobal) :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FormViewModel::class.java)){
            return FormViewModel(askQuestionToPersonalSave,askQuestionsToSaveGlobal) as T
        }
        throw IllegalArgumentException("ViewModel class not found!")
    }
}