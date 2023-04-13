package devcom.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.ui.activity.signin.SignInUseCase
import devcom.android.users.User
import devcom.android.utils.constants.FirebaseConstants

class SignInViewModel(private val signInWithUseCase: SignInUseCase) : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val usersRef = db.collection("users")

    private val _isSignedIn = MutableLiveData<Boolean>()
    private val _isUsedSameEmail = MutableLiveData<Boolean>()
    private val _isUsedSameUsername = MutableLiveData<Boolean>()

    val isUsedSameUsername: LiveData<Boolean>
        get() = _isUsedSameUsername
    val isUsedSameEmail: LiveData<Boolean>
        get() = _isUsedSameEmail
    val isSignedIn: LiveData<Boolean>
        get() = _isSignedIn

    //Use-case katmanı
    fun signInAndUpGoogle(account: GoogleSignInAccount) {
        signInWithUseCase.signInAndUpGoogle(account,
            onSuccess = {_isSignedIn.value = true },
            onFailure = {_isSignedIn.value = false})
    }

    fun authFetchEmail(email: String, password: String, Username: String) {
        usersRef.whereEqualTo(FirebaseConstants.FIELD_USERNAME, Username).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.size() > 0) {
                    _isUsedSameUsername.value = true
                } else {

                    auth.fetchSignInMethodsForEmail(email).addOnSuccessListener {
                        if (it.signInMethods!!.size > 0) {
                            _isUsedSameEmail.value = true
                        } else {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                                            .add(User(Username, auth.currentUser!!.uid, "User"))
                                            .addOnSuccessListener {
                                                _isSignedIn.value = true
                                            }.addOnFailureListener {
                                                _isSignedIn.value = true
                                            }
                                    } else {
                                        _isSignedIn.value = false
                                    }
                                }
                        }
                    }
                }
            }
    }

    fun signInWithFacebook(account: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(account.token)

        auth.signInWithCredential(credential).addOnSuccessListener {
            if (it.additionalUserInfo!!.isNewUser) {
                val user = auth.currentUser
                //Facebook users have a username set "undefined"
                db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                    .add(User("Undefined", user!!.uid, "User"))
                    .addOnSuccessListener {
                        _isSignedIn.value = true
                    }.addOnFailureListener {
                        _isSignedIn.value = false
                    }
            } else {
                _isSignedIn.value = true
            }
        }.addOnFailureListener {
            _isSignedIn.value = false
        }
    }


}