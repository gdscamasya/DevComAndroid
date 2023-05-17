package devcom.android.logic.usecase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import devcom.android.utils.constants.FirebaseConstants
import java.util.*

class AskQuestionsToSaveGlobal(private val auth: FirebaseAuth, private val db: FirebaseFirestore, private val storage: FirebaseStorage) {

    fun askQuestionToSaveGlobal( questionContent: String, questionHeader: String,questionTags: String ,selectedPicture:Uri?, onSucces : () -> Unit, onFailure : () -> Unit) {

        lateinit var getUsername : String

        GetData(auth,db,storage).getData(onSucces = {username ->
            getUsername = username
        },onFailure = {
            onFailure()
        })

        val uuids = UUID.randomUUID()
        val imageName = "$uuids.jpg"

        val reference = storage.reference
        val imageRef = reference.child("questionImages").child(imageName)

        if (selectedPicture == null) {
            val questions = hashMapOf<String, Any>(
                "QuestionUsername" to getUsername,
                "QuestionContent" to questionContent,
                "QuestionHeader" to questionHeader,
                "QuestionTags" to questionTags
            )

            db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)
                .add(questions)
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
                        "QuestionUsername" to getUsername,
                        "QuestionContent" to questionContent,
                        "QuestionHeader" to questionHeader,
                        "QuestionTags" to questionTags,
                        "QuestionImage" to downloadUrl
                    )

                    db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)
                        .add(questions)
                        .addOnSuccessListener {
                            onSucces()
                        }.addOnFailureListener {
                            onFailure()
                        }

                }
            }.addOnFailureListener{
                onFailure()
            }
        }
    }


}