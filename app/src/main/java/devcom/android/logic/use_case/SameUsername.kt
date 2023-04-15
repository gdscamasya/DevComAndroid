package devcom.android.logic.use_case

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.utils.constants.FirebaseConstants

class SameUsername(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {

    private val usersRef = db.collection(FirebaseConstants.COLLECTION_PATH_USERS)

    fun sameUsername(Username: String, onSuccess: () -> Unit, onFailure: () -> Unit){
        usersRef.whereEqualTo(FirebaseConstants.FIELD_USERNAME, Username).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.size() > 0) {
                    onSuccess()//Same username error

                } else {
                    onFailure()
                }

            }
    }

}