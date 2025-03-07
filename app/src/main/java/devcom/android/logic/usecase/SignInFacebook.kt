package devcom.android.logic.usecase

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.data.User
import devcom.android.utils.constants.FirebaseConstants

class SignInFacebook(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {


    fun signInFacebook(account: AccessToken, onSuccess: () -> Unit, onFailure: (errorMessage: String) -> Unit) {
        val credential = FacebookAuthProvider.getCredential(account.token)


        val request = GraphRequest.newMeRequest(account){ `object`, _ ->

            val email = `object`?.getString("email")
            val fullName = `object`?.getString("name")


            auth.fetchSignInMethodsForEmail(email!!).addOnSuccessListener {
                if(it.signInMethods!!.size > 0 && (it.signInMethods!![0].equals("password") || it.signInMethods!![0].equals("google.com"))){
                    onFailure("Bu email adresi daha önce kullanılmış")
                }else{
                    auth.signInWithCredential(credential).addOnSuccessListener {
                        if (it.additionalUserInfo!!.isNewUser) {
                            val user = auth.currentUser
                            //Facebook users have a username set "undefined"
                            db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                                .add(User(fullName, user!!.uid, "User"))
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { onFailure("Bir şeyler yanlış gitti, tekrar deneyiniz") }
                        } else {
                            onSuccess()
                        }
                    }.addOnFailureListener {
                        onFailure("Bir şeyler yanlış gitti, tekrar deneyiniz")
                    }
                }
            }

        }

        val parameters = Bundle()
        parameters.putString("fields","email,name")
        request.parameters = parameters

        request.executeAsync()



    }

}