package devcom.android.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import devcom.android.logic.usecase.AnswerQuestionToSaveGlobal
import devcom.android.logic.usecase.AnswerQuestionToSavePersonal

class AnswerViewModel(
    private val answerQuestionToSaveGlobal: AnswerQuestionToSaveGlobal,
    private val answerQuestionToSavePersonal: AnswerQuestionToSavePersonal
) : ViewModel() {

    private val _isAnswerQuestionPersonal = MutableLiveData<Boolean>()
    val isAnswerQuestionPersonal: LiveData<Boolean>
        get() = _isAnswerQuestionPersonal

    private val _isAnswerQuestionGlobal = MutableLiveData<Boolean>()
    val isAnswerQuestionGlobal: LiveData<Boolean>
        get() = _isAnswerQuestionGlobal

    suspend fun answerQuestionToPersonal(context: Context, questionContent: String) {
        answerQuestionToSavePersonal.answerQuestionToSavePersonal(context, questionContent,
            onSucces = { _isAnswerQuestionPersonal.value = true },
            onFailure = { _isAnswerQuestionPersonal.value = false }
        )
    }

    suspend fun answerQuestionToGlobal(profileImageUrl: String?,
        docId: String, questionContent: String
    ) {
        answerQuestionToSaveGlobal.answerQuestionToSaveGlobal(profileImageUrl,docId, questionContent,
            onSucces = { _isAnswerQuestionGlobal.value = true },
            onFailure = { _isAnswerQuestionGlobal.value = false })
    }

}