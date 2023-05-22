package devcom.android.ui.fragment.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import devcom.android.R
import devcom.android.data.repository.DataStoreRepository
import devcom.android.ui.fragment.form.adapter.QuestionAdapter
import devcom.android.users.Question
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.launch

private lateinit var auth: FirebaseAuth
private lateinit var storage: FirebaseStorage
private lateinit var questionList: ArrayList<Question>
private lateinit var questionAdapter:QuestionAdapter
private lateinit var questionRecyclerView:RecyclerView
private lateinit var likedQuestions: ArrayList<String?>
lateinit var likedIndexQuestions: ArrayList<Int?>


class QuestionsFragment : Fragment() {

    lateinit var dataStoreRepository: DataStoreRepository
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        storage = Firebase.storage

        likedQuestions = ArrayList()
        likedIndexQuestions = ArrayList()
        questionList = ArrayList()

        getData()

        viewLifecycleOwner.lifecycleScope.launch {
            checkLiked()
        }

        questionRecyclerView = view.findViewById(R.id.rv_question)

        questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        questionAdapter = QuestionAdapter(questionList)
        questionRecyclerView.adapter = questionAdapter

    }

    /* private fun refreshSetOnListener(){
        swipeRefreshLayout.setOnRefreshListener {
            questionList.clear()
            if(questionList.isEmpty()){
                getData()
            }
            questionAdapter = QuestionAdapter(questionList)
            questionRecyclerView.adapter = questionAdapter
            swipeRefreshLayout.isRefreshing = false
        }
    }

     */

    private suspend fun checkLiked() {
        dataStoreRepository = DataStoreRepository(requireContext())

        val documents = dataStoreRepository.getDataFromDataStore("document")
        if (documents != null) {
            db.collection(FirebaseConstants.COLLECTION_PATH_USERS).document(documents).collection("LikedQuestions")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(requireContext(), "Beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
                    } else {
                        if (value != null && !value.isEmpty) {
                            val documents = value.documents

                            likedQuestions.clear()

                            for (document in documents) {
                                val docNum = document.id
                                likedQuestions.add(docNum)
                            }

                            processLikedQuestions()
                        }
                    }
                }
        }
    }

    private fun processLikedQuestions() {

            for ((index, question) in questionList.withIndex()) {
                if (likedQuestions.contains(question.docNum)) {
                    likedIndexQuestions.add(index)
                }
            }
    }


    private fun getData(){

        db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS).addSnapshotListener{ value, error ->
            if(error != null){
                Toast.makeText(requireContext(), "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        questionList.clear()

                        for(document in documents){

                            val docNum = document.id
                            val askingUsername = document.get(FirebaseConstants.FIELD_QUESTION_USERNAME) as? String
                            val questionContent = document.get(FirebaseConstants.FIELD_QUESTION_CONTENT) as? String
                            val questionHeader = document.get(FirebaseConstants.FIELD_QUESTION_HEADER) as? String
                            val questionImage = document.get(FirebaseConstants.FIELD_QUESTION_IMAGE) as? String
                            val questionTags = document.get(FirebaseConstants.FIELD_QUESTION_TAGS) as? String
                            val questionProfileImage = document.get(FirebaseConstants.FILED_QUESTION_PROFILE_IMAGE) as? String
                            val questionPoint = document.getLong(FirebaseConstants.FILED_QUESTION_POINT)

                            val askingQuestions = Question(docNum,questionProfileImage,askingUsername,questionContent,questionHeader,questionImage,questionTags,questionPoint.toString())
                            questionList.add(askingQuestions)

                        }
                        questionAdapter.submitData(questionList)
                    }
                }
            }
        }
    }

}