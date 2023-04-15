package devcom.android.logic.use_case

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.users.User
import devcom.android.utils.constants.FirebaseConstants

class SignUpEmail(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {

    fun signUpAccount(email: String, password: String, Username: String, onSuccess: () -> Unit, onFailure: () -> Unit,onSameEmail: () -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnSuccessListener {
            if(it.signInMethods!!.size > 0 && it.signInMethods!![0].equals("password")){
                onSameEmail()
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