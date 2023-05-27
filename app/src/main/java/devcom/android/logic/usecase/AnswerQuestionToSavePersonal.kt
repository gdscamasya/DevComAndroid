package devcom.android.logic.usecase

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.data.repository.DataStoreRepository
import devcom.android.utils.constants.FirebaseConstants
import java.util.UUID

var uuidAnswer: String? = null

class AnswerQuestionToSavePersonal(private val db: FirebaseFirestore) {

    private lateinit var dataStoreRepository: DataStoreRepository

    suspend fun answerQuestionToSavePersonal(context: Context, answerContent: String, onSucces : () -> Unit, onFailure : () -> Unit){

        val uuidIn = UUID.randomUUID()
        uuidAnswer = uuidIn.toString()

        dataStoreRepository = DataStoreRepository(context)

        val answerList = hashMapOf(
            "AnswerContent" to answerContent
        )

        val document = dataStoreRepository.getDataFromDataStore("document")

        if (document != null) {
            db.collection(FirebaseConstants.COLLECTION_PATH_USERS).document(document)
                .collection("HerAnswers").document(uuidAnswer.toString())
                .set(answerList).addOnSuccessListener {
                    onSucces()
                }.addOnFailureListener {
                    onFailure()
                }
        }else{
            onFailure()
        }




    }


}