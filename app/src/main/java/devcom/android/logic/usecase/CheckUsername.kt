package devcom.android.logic.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.utils.constants.FirebaseConstants
import java.lang.Error

class CheckUsername(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {

    private val usersRef = db.collection(FirebaseConstants.COLLECTION_PATH_USERS)

    fun checkUsername(username: String, onSuccess: () -> Unit, onFailure: (errorMessage: String) -> Unit){
        usersRef.whereEqualTo(FirebaseConstants.FIELD_USERNAME, username).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.size() > 0) {
                    onFailure("Bu kullanıcı adı daha önce kullanılmış")//High order func
                } else {
                    onSuccess()//Same username error
                }

            }
    }

}