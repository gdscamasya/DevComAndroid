package devcom.android.logic.usecase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.data.repository.DataStoreRepository
import devcom.android.utils.constants.FirebaseConstants
import java.util.UUID


    var uuid: String? = null

class AskQuestionToPersonalSave(private val auth: FirebaseAuth, private val db: FirebaseFirestore){
//Kişinin sormuş olduğu soruları user -> documents -> collections -> HerQuestion -> Documents -> field şeklinde kayıt ediyoruz
     lateinit var dataStoreRepository: DataStoreRepository

     suspend fun askQuestionToPersonal(context: Context, questionContent: String, questionHeader: String, onSucces : () -> Unit, onFailure : () -> Unit){

         val uuidIn = UUID.randomUUID()
         uuid = uuidIn.toString()

        dataStoreRepository = DataStoreRepository(context)

        val questionList = hashMapOf(
            "QuestionContent" to questionContent,
            "QuestionHeader" to questionHeader
        )

        val document = dataStoreRepository.getDataFromDataStore("document")        

        if (document != null) {
            db.collection(FirebaseConstants.COLLECTION_PATH_USERS).document(document)
                .collection("HerQuestion").document(uuid.toString())
                .set(questionList).addOnSuccessListener {
                    onSucces()
                }.addOnFailureListener {
                    onFailure()
                }
        }else{
            onFailure()
        }




    }



}