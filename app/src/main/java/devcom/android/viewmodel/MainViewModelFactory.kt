package devcom.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import devcom.android.logic.usecase.*

class MainViewModelFactory(private val signInGoogle: SignInGoogle,
                           private val signInFacebook: SignInFacebook,
                           private val sameUsername: CheckUsername,
                           private val signUpEmail: SignUpEmail
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(signInGoogle,signInFacebook,sameUsername,signUpEmail) as T
        }
        throw IllegalArgumentException("ViewModel class not found!")
    }

}