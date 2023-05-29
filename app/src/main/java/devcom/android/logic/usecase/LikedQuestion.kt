package devcom.android.logic.usecase

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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

class LikedQuestion(private val db: FirebaseFirestore) {

    lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var pointText: TextView
    private lateinit var liking: ImageView
    private lateinit var unLiking: ImageView


    private var point: Long = 0
    fun likedQuestions(
        view: View,
        context: Context,
        questionList: ArrayList<Question>,
        position: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        pointText = view.findViewById(R.id.tv_up)
        liking = view.findViewById(R.id.iv_liking)
        unLiking = view.findViewById(R.id.iv_liked)



        val collectRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)


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
                    }
                    .addOnFailureListener {
                        onFailure()
                    }
            }
        }

        collectRef.whereEqualTo(
            FirebaseConstants.FIELD_QUESTION_HEADER,
            questionList.get(position).questionHeader
        )
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    point = document.getLong("Point")!!

                    point++

                    val updates = hashMapOf<String, Any>(
                        "Point" to point
                    )

                    document.reference.update(updates)

                    pointText.text = point.toString()
                    liking.invisible()
                    unLiking.visible()
                    questionList[position].questionPoint = point.toString()

                }
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }

    }




}