package devcom.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.logic.use_case.*

class MainViewModel(private val signInGoogle: SignInGoogle,private val signInFacebook: SignInFacebook,private val sameUsername: SameUsername,private val signUpEmail: SignUpEmail) : ViewModel() {

    private val _isSignedGoogleIn = MutableLiveData<Boolean>()
    private val _isSignedIn = MutableLiveData<Boolean>()
    private val _isSignedFacebookIn = MutableLiveData<Boolean>()
    private val _isUsedSameEmail = MutableLiveData<Boolean>()
    private val _isUsedSameUsername = MutableLiveData<Boolean>()

    val isSignUp: LiveData<Boolean>
        get() = _isSignedIn
    val isSignedFacebookIn: LiveData<Boolean>
        get() = _isSignedFacebookIn
    val isUsedSameUsername: LiveData<Boolean>
        get() = _isUsedSameUsername
    val isUsedSameEmail: LiveData<Boolean>
        get() = _isUsedSameEmail
    val isSignedGoogleIn: LiveData<Boolean>
        get() = _isSignedGoogleIn

    //Use-case katmanı
    fun signInGoogle(account: GoogleSignInAccount) {
        signInGoogle.signInGoogle(account,
            onSuccess = { _isSignedGoogleIn.value = true },
            onFailure = { _isSignedGoogleIn.value = false })
    }

    fun signInFacebook(account: AccessToken,email: String) {
        signInFacebook.signInFacebook(account,
            onSameEmail = {_isUsedSameEmail.value = true},
            onSuccess = { _isSignedFacebookIn.value = true },
            onFailure = { _isSignedFacebookIn.value = false })
    }

    fun sameUsername(Username: String) {
        sameUsername.sameUsername(Username,
            onSuccess = { _isUsedSameUsername.value = true },
            onFailure = { _isUsedSameUsername.value = false })
    }



    fun signUpEmail(email: String, password: String, Username: String) {
        signUpEmail.signUpAccount(email, password, Username,
            onSameEmail = { _isUsedSameEmail.value = true },
            onSuccess = { _isSignedIn.value = true },
            onFailure = { _isSignedIn.value = false })
    }



}