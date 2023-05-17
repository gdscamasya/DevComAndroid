package devcom.android.ui.fragment.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import devcom.android.R
import devcom.android.ui.fragment.form.adapter.QuestionAdapter
import devcom.android.users.Question
import devcom.android.utils.constants.FirebaseConstants

private lateinit var auth: FirebaseAuth
private lateinit var storage: FirebaseStorage
private lateinit var questionList: ArrayList<Question>
private lateinit var questionAdapter:QuestionAdapter

private lateinit var questionRecyclerView:RecyclerView

val db = Firebase.firestore

class QuestionsFragment : Fragment() {

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
        questionList = ArrayList<Question>()

        getData()

        questionRecyclerView = view.findViewById(R.id.rv_question)

        questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        questionAdapter = QuestionAdapter(questionList)
        questionRecyclerView.adapter = questionAdapter



    }


    private fun getData(){

        db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS).addSnapshotListener{ value, error ->
            if(error != null){
                Toast.makeText(requireContext(), "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(document in documents){

                            val askingUsername = document.get(FirebaseConstants.FIELD_QUESTION_USERNAME) as String
                            val questionContent = document.get(FirebaseConstants.FIELD_QUESTION_CONTENT) as String
                            val questionHeader = document.get(FirebaseConstants.FIELD_QUESTION_HEADER) as String
                            val questionImage = document.get(FirebaseConstants.FIELD_QUESTION_IMAGE) as String
                            val questionTags = document.get(FirebaseConstants.FIELD_QUESTION_TAGS) as String

                            val askingQuestions = Question(askingUsername,questionContent,questionHeader,questionImage,questionTags)
                            questionList.add(askingQuestions)

                        }
                        questionAdapter.notifyDataSetChanged()
                    }
                }
            }

        }
        for (items in questionList) {
            println(items)
        }
    }


}