package devcom.android.logic.use_case

import com.google.firebase.auth.FirebaseAuth

class SameEmail(private val auth: FirebaseAuth) {

    fun sameEmail(email: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnSuccessListener {
            if (it.signInMethods!!.size > 0 && it.signInMethods!![0].equals("password")) {
                onSuccess()
            } else {
                onFailure()
            }
        }.addOnFailureListener {
            onSuccess()
        }
    }
}