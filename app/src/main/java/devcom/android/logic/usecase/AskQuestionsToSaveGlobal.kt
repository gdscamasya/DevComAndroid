package devcom.android.logic.usecase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import devcom.android.data.Question
import devcom.android.ui.fragment.form.questionList
import devcom.android.ui.fragment.form.questionRecyclerAdapter
import devcom.android.ui.fragment.form.topQuestionRecyclerAdapter
import devcom.android.ui.fragment.form.topQuestionList
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.*
import java.util.*

class AskQuestionsToSaveGlobal(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    suspend fun askQuestionToSaveGlobal(
        profileImageUrl: String? = null,
        questionContent: String,
        questionHeader: String,
        questionTags: String,
        selectedPicture: Uri?,
        onSucces: () -> Unit,
        onFailure: () -> Unit
    ) {

        lateinit var getUsername: String
        val point = 0

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
                if (selectedPicture == null) {
                    if(profileImageUrl != null){

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

                    }else{

                        val questions = hashMapOf<String, Any>(
                            "Point" to point,
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

                    }

                    questionList.add(Question(uuid.toString(),profileImageUrl,getUsername,questionContent,questionHeader,selectedPicture.toString(),questionTags,point.toString(),false))
                    topQuestionList.add(Question(uuid.toString(),profileImageUrl,getUsername,questionContent,questionHeader,selectedPicture.toString(),questionTags,point.toString(),false))
                    topQuestionRecyclerAdapter.setData(topQuestionList)
                    questionRecyclerAdapter.setData(questionList)


                } else {
                    val uuids = UUID.randomUUID()
                    val imageName = "$uuids.jpg"

                    val reference = storage.reference
                    val imageRef = reference.child("questionImages").child(imageName)

                    imageRef.putFile(selectedPicture).addOnSuccessListener {
                        //download url -> firestore a aktaracaz
                        val uploadPicRef = storage.reference.child("questionImages").child(imageName)
                        uploadPicRef.downloadUrl.addOnSuccessListener {
                            val downloadUrl = it.toString()
                            if(profileImageUrl != null){
                                val questions = hashMapOf<String, Any>(
                                    "Point" to point,
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
                            }else{
                                val questions = hashMapOf<String, Any>(
                                    "Point" to point,
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


                            questionList.add(Question(uuid.toString(),profileImageUrl,getUsername,questionContent,questionHeader,selectedPicture.toString(),questionTags,point.toString(),false))
                            topQuestionList.add(Question(uuid.toString(),profileImageUrl,getUsername,questionContent,questionHeader,selectedPicture.toString(),questionTags,point.toString(),false))
                            topQuestionRecyclerAdapter.setData(topQuestionList)
                            questionRecyclerAdapter.setData(questionList)

                        }
                    }.addOnFailureListener {
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