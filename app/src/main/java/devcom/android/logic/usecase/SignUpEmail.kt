package devcom.android.logic.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.data.User
import devcom.android.utils.constants.FirebaseConstants

class SignUpEmail(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {

    fun signUpAccount(email: String, password: String, Username: String, onSuccess: () -> Unit, onFailure: () -> Unit,onSameEmail: (errorMessage: String) -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnSuccessListener { signInMethodQueryResult ->
            if(signInMethodQueryResult.signInMethods!!.size > 0){
                onSameEmail("Bu email adresi daha önce kullanılmış")
            }else{
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                                .add(User(Username, auth.currentUser!!.uid, "User"))
                                .addOnSuccessListener {
                                    onSuccess()
                                }.addOnFailureListener {
                                    onFailure()
                                }
                        } else {
                            onFailure()
                        }
                    }
            }
        }



    }

}