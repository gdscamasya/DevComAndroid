package devcom.android.logic.use_case

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.users.User
import devcom.android.utils.constants.FirebaseConstants

class SignInGoogle(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {


    /*fun signInAndUpFacebook(account: AccessToken) {
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

    fun signUpAccount(email: String, password: String, Username: String) {
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

     */


    fun signInGoogle(
        account: GoogleSignInAccount,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            val currentUser = auth.currentUser
            if (it.additionalUserInfo!!.isNewUser) {
                db.collection("users").document(currentUser!!.uid)
                    .set(User(currentUser.displayName, currentUser.email, "User"))
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure() }
            } else {
                onSuccess()
            }
        }.addOnFailureListener {
            onFailure()
        }
    }


}