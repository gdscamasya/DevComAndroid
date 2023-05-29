package devcom.android.logic.usecase

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.R
import devcom.android.data.repository.DataStoreRepository
import devcom.android.data.Question
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.invisible
import devcom.android.utils.extensions.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckLikedQuestions(private val db: FirebaseFirestore) {

    lateinit var dataStoreRepository: DataStoreRepository
    lateinit var likedIndexQuestions:ArrayList<Int?>
    private lateinit var likedQuestions: ArrayList<String?>

     fun checkLiked(
         view:View,
         context: Context,
         questionList: ArrayList<Question>,
         onSuccess: () -> Unit,
         onFailure: () -> Unit
    ) {



         CoroutineScope(Dispatchers.Main).launch {
             dataStoreRepository = DataStoreRepository(context)
             val documents = dataStoreRepository.getDataFromDataStore("document")

             likedQuestions = ArrayList()
             likedIndexQuestions = ArrayList()

             if (documents != null) {
                 db.collection(FirebaseConstants.COLLECTION_PATH_USERS).document(documents)
                     .collection("LikedQuestions")
                     .addSnapshotListener { value, error ->
                         if (error != null) {
                             onFailure()
                             return@addSnapshotListener
                         } else {
                             if (value != null && !value.isEmpty) {
                                 val documenter = value.documents

                                 likedQuestions.clear()


                                 for (document in documenter) {
                                     val docNum = document.id
                                     likedQuestions.add(docNum) ///users -> 4vX7ac7koaOTmu1LznCB -> LikedQuestions -> document.id
                                 }

                                 for ((index, question) in questionList.withIndex()) {
                                     if (likedQuestions.contains(question.docNum)) {
                                         likedIndexQuestions.add(index)
                                     }
                                 }

                                 if(questionList.isNotEmpty()){
                                     for(liking in likedQuestions){
                                         for(question in questionList){
                                             if(liking == question.docNum){
                                                //val questionIndex = questionList.indexOf(question)
                                                 Log.i("içerideyiz",question.likingViewVisible.toString())
                                                question.likingViewVisible = true
                                             }
                                         }
                                     }
                                 }



                                 onSuccess()
                             }
                         }
                     }
             }
         }

    }

}