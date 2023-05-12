package devcom.android.logic.usecase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.data.repository.DataStoreRepository
import devcom.android.utils.constants.FirebaseConstants

class AskQuestionToPersonalSave(private val auth: FirebaseAuth, private val db: FirebaseFirestore){
//Kişinin sormuş olduğu soruları user -> documents -> collections -> HerQuestion -> Documents -> field şeklinde kayıt ediyoruz
     lateinit var dataStoreRepository: DataStoreRepository

     suspend fun askQuestionToPersonal(context: Context, questionContent: String, questionHeader: String, onSucces : () -> Unit, onFailure : () -> Unit){

        dataStoreRepository = DataStoreRepository(context)

        val questionList = hashMapOf(
            "QuestionContent" to questionContent,
            "QuestionHeader" to questionHeader
        )

        val document = dataStoreRepository.getDataFromDataStore("document")
        Log.i("docmentsss",document.toString())

        if (document != null) {
            db.collection(FirebaseConstants.COLLECTION_PATH_USERS).document(document)
                .collection("HerQuestion")
                .add(questionList).addOnSuccessListener {
                    onSucces()
                }.addOnFailureListener {
                    onFailure()
                }
        }else{
            onFailure()
        }




    }



}