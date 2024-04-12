package devcom.android.logic.usecase

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.data.repository.DataStoreRepository
import devcom.android.data.Question
import devcom.android.ui.fragment.form.questionRecyclerAdapter
import devcom.android.ui.fragment.form.topQuestionRecyclerAdapter
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckLikedQuestions(private val db: FirebaseFirestore) {

    lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var likedQuestions: ArrayList<String?>

    fun checkLiked(
        view: View,
        context: Context,
        questionList: ArrayList<Question>,
        listName: String
    ) {


        CoroutineScope(Dispatchers.Main).launch {
            dataStoreRepository = DataStoreRepository(context)
            val documents = dataStoreRepository.getDataFromDataStore("document")

            Log.i("documentName",documents.toString())

            likedQuestions = ArrayList()

            if (documents != null) {
                db.collection(FirebaseConstants.COLLECTION_PATH_USERS).document(documents)
                    .collection("LikedQuestions")
                    .addSnapshotListener { value, error ->

                        if (error != null) {
                            Toast.makeText(context, "sadoaw", Toast.LENGTH_SHORT).show()
                        } else {

                            if (value != null) {
                                if (!value.isEmpty) {
                                    val documenter = value.documents


                                    likedQuestions.clear()

                                    //User get liking questions with document.id
                                    getDocumentId(documenter)

                                    if (questionList.isNotEmpty()){

                                        for (question in questionList){
                                            Log.i("allQuestionListFirst",question.toString())
                                        }

                                        for (question in questionList) {
                                            if (likedQuestions.contains(question.docNum)) {
                                                question.likingViewVisible = true
                                            }
                                        }

                                        for (question in questionList){
                                            Log.i("allQuestionListSecond",question.toString())
                                        }
                                        if(listName == "QuestionList"){
                                            questionRecyclerAdapter.setData(questionList)
                                        }else if(listName == "TopQuestionList"){
                                            topQuestionRecyclerAdapter.setData(questionList)

                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }

    }

    private fun getDocumentId(documentId: MutableList<DocumentSnapshot>) {
        for (document in documentId) {
            val docNum = document.id
            likedQuestions.add(docNum) ///users -> 4vX7ac7koaOTmu1LznCB -> LikedQuestions -> document.id
        }
    }

}