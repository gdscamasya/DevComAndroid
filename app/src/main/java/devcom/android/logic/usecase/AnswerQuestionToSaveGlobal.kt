package devcom.android.logic.usecase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class AnswerQuestionToSaveGlobal(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {


    suspend fun answerQuestionToSaveGlobal(
        profileImageUrl: String? = null,
        docId: String,
        answerContent: String,
        chooseImageList: ArrayList<Uri>?,
        onSucces: () -> Unit,
        onFailure: () -> Unit
    ) {

        lateinit var getUsername: String
        val uuidIn = UUID.randomUUID()

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
                val convertUrlList = convertImageToUrl(chooseImageList)

                val answerData = hashMapOf<String, Any?>(
                    "AnswerContent" to answerContent,
                    "AnswerUsername" to getUsername,
                    "AnswerProfileImage" to profileImageUrl,
                    "AnswerImgUrlList" to convertUrlList
                )

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
                //data could not be received!
                onFailure()

            }
        }
    }
    private suspend fun convertImageToUrl(chooseImageList: ArrayList<Uri>?): ArrayList<String> {
        val urlList = ArrayList<String>()
        chooseImageList?.forEachIndexed { index, image ->
            val imageRef = storage.reference.child("questionImages")
                .child("$uuidAnswer+$index.jpg").downloadUrl.await()
            urlList.add(imageRef.toString())
        }
        return urlList
    }

}