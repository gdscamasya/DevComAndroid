package devcom.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import devcom.android.logic.usecase.*
import devcom.android.ui.activity.main.MainActivity
import devcom.android.utils.Resource
import devcom.android.utils.extensions.navigateToAnotherActivity

class MainViewModel(
    private val signInGoogle: SignInGoogle,
    private val signInFacebook: SignInFacebook,
    private val checkUsername: CheckUsernameUseCase,
    private val signUpEmail: SignUpEmail
) : ViewModel() {

    private val _isSignInGoogle = MutableLiveData<Boolean>()
    val isSignInGoogle: LiveData<Boolean>
        get() = _isSignInGoogle

    private val _isSignUp = MutableLiveData<Boolean>()
    val isSignUp: LiveData<Boolean>
        get() = _isSignUp

    private val _isSignedFacebookIn = MutableLiveData<Resource<Void>>()
    val isSignInFacebook: LiveData<Resource<Void>>
        get() = _isSignedFacebookIn

    private val _isExistsEmail = MutableLiveData<String>()
    val isExistsEmail: LiveData<String>
        get() = _isExistsEmail

    private val _isExistsEmailFacebook = MutableLiveData<String>()
    val isExistsEmailFacebook: LiveData<String>
        get() = _isExistsEmailFacebook

    private val _isExistsUsername = MutableLiveData<String>()
    val isExistsUsername: LiveData<String>
        get() = _isExistsUsername


    fun signInGoogle(account: GoogleSignInAccount) {
        signInGoogle.signInGoogle(account,
            onSuccess = { _isSignInGoogle.value = true },
            onFailure = { _isSignInGoogle.value = false })
    }

    fun signInFacebook(account: AccessToken) {
        signInFacebook.signInFacebook(account,
            onSuccess = {
                _isSignedFacebookIn.value = Resource.Success()
            },
            onFailure = {
                _isSignedFacebookIn.value = Resource.Error(it)
            })
    }

    fun checkUsername(email: String, password: String, username: String) {
        checkUsername.checkUsername(username,
            onSuccess = {
                signUpEmail(email, password, username)
            },
            onFailure = {
                _isExistsUsername.value = it
            })
    }


    fun signUpEmail(email: String, password: String, Username: String) {
        signUpEmail.signUpAccount(email, password, Username,
            onSameEmail = { _isExistsEmail.value = it },
            onSuccess = { _isSignUp.value = true },
            onFailure = { _isSignUp.value = false })
    }


}