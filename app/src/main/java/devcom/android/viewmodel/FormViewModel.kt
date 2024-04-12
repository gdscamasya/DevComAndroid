package devcom.android.viewmodel

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import devcom.android.logic.usecase.AskQuestionToPersonalSave
import devcom.android.logic.usecase.AskQuestionsToSaveGlobal
import devcom.android.logic.usecase.GetData

class FormViewModel(private val askQuestionToPersonalSave: AskQuestionToPersonalSave, private val askQuestionsToSaveGlobal: AskQuestionsToSaveGlobal):ViewModel() {

    private val _isAskQuestionPersonal = MutableLiveData<Boolean>()
    val isAskQuestionPersonal: LiveData<Boolean>
        get() = _isAskQuestionPersonal

    private val _isAskQuestion = MutableLiveData<Boolean>()
    val isAskQuestion: LiveData<Boolean>
        get() = _isAskQuestion

    suspend fun askQuestionToPersonal(context: Context, questionContent: String, questionHeader: String,pending: Boolean){
        askQuestionToPersonalSave.askQuestionToPersonal(context,questionContent, questionHeader,pending,
            onSucces = { _isAskQuestionPersonal.value = true},
            onFailure = { _isAskQuestionPersonal.value = false}
            )
    }

    suspend fun askQuestionToSaveGlobal(profileImageUrl: String?,
        questionContent: String, questionHeader: String,itemSelected: String,
        selectedPicture: Uri?,pending: Boolean
    ){
        askQuestionsToSaveGlobal.askQuestionToSaveGlobal(profileImageUrl,questionContent, questionHeader,itemSelected, selectedPicture,pending,
        onSucces = { _isAskQuestion.value = true },
        onFailure = { _isAskQuestion.value = false })
    }



}