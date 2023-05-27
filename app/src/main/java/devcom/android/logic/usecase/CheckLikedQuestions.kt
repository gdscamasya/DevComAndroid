package devcom.android.logic.usecase

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import devcom.android.R
import devcom.android.data.repository.DataStoreRepository
import devcom.android.ui.fragment.form.adapter.QuestionRecyclerAdapter
import devcom.android.users.Question
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.invisible
import devcom.android.utils.extensions.visible

class CheckLikedQuestions(private val db: FirebaseFirestore) {

    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var liking:ImageView
    private lateinit var unLiking:ImageView
    suspend fun checkLiked(
        view:View,
        context: Context,
        likedQuestions: ArrayList<String?>,
        questionList: ArrayList<Question>,
        likedIndexQuestions: ArrayList<Int?>,
    ) {

        dataStoreRepository = DataStoreRepository(context)

        liking = view.findViewById(R.id.iv_up)
        unLiking = view.findViewById(R.id.iv_down)


        val documents = dataStoreRepository.getDataFromDataStore("document")
        if (documents != null) {
            db.collection(FirebaseConstants.COLLECTION_PATH_USERS).document(documents)
                .collection("LikedQuestions")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(context, "Beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (value != null && !value.isEmpty) {
                            val documents = value.documents

                            likedQuestions.clear()

                            for (document in documents) {
                                val docNum = document.id
                                likedQuestions.add(docNum)
                            }

                            for ((index, question) in questionList.withIndex()) {
                                if (likedQuestions.contains(question.docNum)) {
                                    likedIndexQuestions.add(index)
                                }
                            }

                            for(likings in devcom.android.ui.fragment.form.likedIndexQuestions){
                                Log.i("sayi",likings.toString())
                                liking.invisible()
                                unLiking.invisible()
                            }

                        }
                    }
                }
        }
    }

}