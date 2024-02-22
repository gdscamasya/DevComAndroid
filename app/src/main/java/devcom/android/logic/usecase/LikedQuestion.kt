package devcom.android.logic.usecase

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.R
import devcom.android.data.repository.DataStoreRepository
import devcom.android.data.Question
import devcom.android.ui.fragment.form.questionRecyclerAdapter
import devcom.android.ui.fragment.form.topQuestionRecyclerAdapter
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LikedQuestion(private val db: FirebaseFirestore) {

    lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var pointText: TextView
    private var point: Long = 0
    fun likedQuestions(
        view: View,
        context: Context,
        questionList: ArrayList<Question>,
        position: Int,
        listName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        pointText = view.findViewById(R.id.tv_up)



        val collectRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)
            .document(questionList[position].docNum!!)


        CoroutineScope(Dispatchers.Main).launch {
            dataStoreRepository = DataStoreRepository(context)
            val document = dataStoreRepository.getDataFromDataStore("document")

            if (document != null) {
                db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                    .document(document)
                    .collection("LikedQuestions").document(questionList[position].docNum!!)
                    .set(emptyMap<String, Any>())
                    .addOnSuccessListener {
                        onSuccess()
                        Log.i("LikedQuestionUpdateUI","Succes")
                    }
                    .addOnFailureListener {
                        onFailure()
                    }
            }
        }

        collectRef.get().addOnSuccessListener {

            point = it.getLong("Point")!!

            point++

            val updates = hashMapOf<String, Any>(
                "Point" to point
            )

            it.reference.update(updates)

            pointText.text = point.toString()
            questionList[position].questionPoint = point.toString()

            questionList.get(position).likingViewVisible = true


            if(listName == "QuestionList"){
                questionRecyclerAdapter.setData(questionList)
            }else if(listName == "TopQuestionList"){
                topQuestionRecyclerAdapter.setData(questionList)
            }


        }.addOnFailureListener {
            onFailure()
        }

    }


}