package devcom.android.logic.usecase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AnswerQuestionToSaveGlobal(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {


    suspend fun answerQuestionToSaveGlobal(
        profileImageUrl: String?,
        docId: String,
        answerContent: String,
        onSucces: () -> Unit,
        onFailure: () -> Unit
    ) {

        lateinit var getUsername: String

        suspend fun getDataWait(): Boolean = withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Boolean>()
            launch {
                try {
                    GetData(auth, db, storage).getData(
                        onSucces = { username ->
                            getUsername = username
                            deferred.complete(true)
                        },
                        onFailure = {
                            onFailure()
                            deferred.complete(false)
                        }
                    )
                } catch (e: Exception) {
                    deferred.complete(false)
                }
            }
            deferred.await()
        }


        CoroutineScope(Dispatchers.Main).launch {
            val result = getDataWait()
            if (result) {

                if(profileImageUrl != null){
                    val answerData = hashMapOf<String, Any>(
                        "AnswerContent" to answerContent,
                        "AnswerUsername" to getUsername,
                        "AnswerProfileImage" to profileImageUrl,
                    )

                    if (docId != null) {
                        db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)
                            .document(docId)
                            .collection(FirebaseConstants.COLLECTION_PATH_ANSWERS)
                            .document(uuidAnswer.toString()).set(answerData)
                            .addOnSuccessListener {
                                onSucces()
                            }.addOnFailureListener {
                                onFailure()
                            }
                    } else {
                        onFailure()
                    }

                }else{
                    val answerData = hashMapOf<String, Any>(
                        "AnswerContent" to answerContent,
                        "AnswerUsername" to getUsername,
                    )

                    if (docId != null) {
                        db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)
                            .document(docId)
                            .collection(FirebaseConstants.COLLECTION_PATH_ANSWERS)
                            .document(uuidAnswer.toString()).set(answerData)
                            .addOnSuccessListener {
                                onSucces()
                            }.addOnFailureListener {
                                onFailure()
                            }
                    } else {
                        onFailure()
                    }
                }





            } else {
                onFailure()
                // Veri alınamadı, hata durumunu işleyin
            }
        }

    }


}