package devcom.android.ui.fragment.form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import devcom.android.ui.fragment.form.adapter.InsideTheQuestionAdapter
import devcom.android.users.InsideQuestion
import devcom.android.utils.constants.FirebaseConstants

private lateinit var auth: FirebaseAuth
private lateinit var storage: FirebaseStorage
private lateinit var insideTheQuestionRecyclerView:RecyclerView
private lateinit var questionTitle:TextView
private lateinit var insideTheQuestionAdapter: InsideTheQuestionAdapter
private lateinit var insideTheQuestionList: ArrayList<InsideQuestion?>



class InsideTheQuestionFragment : Fragment() {

    var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inside_the_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        storage = Firebase.storage

        insideTheQuestionList = ArrayList()

        insideTheQuestionRecyclerView = view.findViewById(R.id.recyclerViewInsideQuestion)
        questionTitle = view.findViewById(R.id.tv_question_inside_header)

        arguments?.let {
            var docId = InsideTheQuestionFragmentArgs.fromBundle(it).docId
            getData(docId)
        }

        insideTheQuestionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        insideTheQuestionAdapter = InsideTheQuestionAdapter(insideTheQuestionList)
        insideTheQuestionRecyclerView.adapter = insideTheQuestionAdapter

    }


    private fun getData(docId: String?){

        val documentRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)

        if (docId != null) {
            documentRef.document(docId).get().addOnSuccessListener {
                if(it.exists()){
                    val questionUsername = it.getString(FirebaseConstants.FIELD_QUESTION_USERNAME)
                    val questionHeader = it.getString(FirebaseConstants.FIELD_QUESTION_HEADER)
                    val questionContent = it.getString(FirebaseConstants.FIELD_QUESTION_CONTENT)
                    val questionProfileImage = it.getString(FirebaseConstants.FILED_QUESTION_PROFILE_IMAGE)

                    val insideQuestionArray = InsideQuestion(questionHeader,questionUsername,questionContent,questionProfileImage)
                    insideTheQuestionList.add(insideQuestionArray)
                    questionTitle.text = questionHeader

                }else{
                    Toast.makeText(requireContext(), "belge bulunamadı", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "Bir şeyler ters Gitti..", Toast.LENGTH_SHORT).show()
            }


        }


    }
}

