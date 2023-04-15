package devcom.android.logic.use_case

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.users.User
import devcom.android.utils.constants.FirebaseConstants

class SignInFacebook(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {


    fun signInFacebook(account: AccessToken, onSuccess: () -> Unit, onFailure: () -> Unit,onSameEmail: () -> Unit) {
        val credential = FacebookAuthProvider.getCredential(account.token)
        //Log.i("sae",account.userId.toString())
        auth.fetchSignInMethodsForEmail(account.userId.toString()).addOnSuccessListener {
            if(it.signInMethods!!.size > 0 && (it.signInMethods!![0].equals("password") || it.signInMethods!![0].equals("facebook.com"))){
                onSameEmail()
            }else{
                auth.signInWithCredential(credential).addOnSuccessListener {
                    if (it.additionalUserInfo!!.isNewUser) {
                        val user = auth.currentUser
                        //Facebook users have a username set "undefined"
                        db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                            .add(User("Undefined", user!!.uid, "User"))
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

    }

}