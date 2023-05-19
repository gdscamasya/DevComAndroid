package devcom.android.logic.usecase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AskQuestionsToSaveGlobal(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    fun askQuestionToSaveGlobal(
        profileImageUrl: String,
        questionContent: String,
        questionHeader: String,
        questionTags: String,
        selectedPicture: Uri?,
        onSucces: () -> Unit,
        onFailure: () -> Unit
    ) {

        lateinit var getUsername: String
        val point = 0
        val uuids = UUID.randomUUID()
        val imageName = "$uuids.jpg"

        val reference = storage.reference
        val imageRef = reference.child("questionImages").child(imageName)

        suspend fun getDataWait(): Boolean = withContext(Dispatchers.IO) {
            var success = false
            val job = launch {
                GetData(auth, db, storage).getData(
                    onSucces = { username ->
                        getUsername = username
                        success = true
                    },
                    onFailure = {
                        onFailure()
                    }
                )
            }
            job.join() // Veri alma işlemi tamamlanana kadar bekle
            success
        }


        CoroutineScope(Dispatchers.Main).launch {
            val result = getDataWait()
            if (result) {
                if (selectedPicture == null) {
                    val questions = hashMapOf<String, Any>(
                        "Point" to point,
                        "AskQuestionProfileImage" to profileImageUrl,
                        "QuestionUsername" to getUsername,
                        "QuestionContent" to questionContent,
                        "QuestionHeader" to questionHeader,
                        "QuestionTags" to questionTags
                    )

                    db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS).document(uuid.toString())
                        .set(questions)
                        .addOnSuccessListener {
                            onSucces()
                        }.addOnFailureListener {
                            onFailure()
                        }
                } else {
                    imageRef.putFile(selectedPicture).addOnSuccessListener {
                        //download url -> firestore a aktaracaz
                        val uploadPicRef = storage.reference.child("questionImages").child(imageName)
                        uploadPicRef.downloadUrl.addOnSuccessListener {
                            val downloadUrl = it.toString()

                            val questions = hashMapOf<String, Any>(
                                "AskQuestionProfileImage" to profileImageUrl,
                                "QuestionUsername" to getUsername,
                                "QuestionContent" to questionContent,
                                "QuestionHeader" to questionHeader,
                                "QuestionTags" to questionTags,
                                "QuestionImage" to downloadUrl
                            )


                            db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)
                                .document(uuid.toString())
                                .set(questions)
                                .addOnSuccessListener {
                                    onSucces()
                                }.addOnFailureListener {
                                    onFailure()
                                }

                        }
                    }.addOnFailureListener {
                        onFailure()
                    }
                }
            } else {
                // Veri alınamadı, hata durumunu işleyin
            }
        }

    }


}