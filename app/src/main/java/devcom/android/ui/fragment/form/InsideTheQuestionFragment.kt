package devcom.android.ui.fragment.form

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
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
import devcom.android.ui.fragment.form.adapter.InsideTheQuestionAdapter
import devcom.android.data.Answer
import devcom.android.data.InsideQuestion
import devcom.android.utils.constants.FirebaseConstants


class InsideTheQuestionFragment : Fragment() {

    var db = Firebase.firestore
    private lateinit var questionTitle:TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var insideTheQuestionRecyclerView:RecyclerView
    private lateinit var insideTheQuestionAdapter: InsideTheQuestionAdapter
    private lateinit var insideTheQuestionList: ArrayList<Any?>
    private lateinit var answerQuestionImageView:ImageView
    private lateinit var returnInsideQuestionImageView:ImageView
    private lateinit var refreshInsideQuestion: SwipeRefreshLayout
    private var fragmentContext: Context? = null
    private var docId: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onDetach() {
        super.onDetach()
        fragmentContext = null
    }
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
        answerQuestionImageView = view.findViewById(R.id.iv_answer_in_question)
        returnInsideQuestionImageView = view.findViewById(R.id.iv_return_inside_question)
        refreshInsideQuestion = view.findViewById(R.id.swipeRefreshInsideQuestionLayout)

        returnInsideQuestionImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        arguments?.let {
            docId = InsideTheQuestionFragmentArgs.fromBundle(it).docId
            getData(docId)
            answerQuestionSetOnClickListener(docId)
        }

        setRefreshLayout()
    }

    private fun setRefreshLayout(){
        refreshInsideQuestion.setOnRefreshListener {
            //getAnswerData(docId)
            refreshInsideQuestion.isRefreshing = false
        }
    }
    private fun answerQuestionSetOnClickListener(docId: String?){
        answerQuestionImageView.setOnClickListener{
            val action = InsideTheQuestionFragmentDirections.actionInsideTheQuestionFragmentToAnswerQuestion(docId)
            Navigation.findNavController(it).navigate(action)
        }
    }


    private fun getData(docId: String?){

        val documentRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS) //Question Document ->

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

                    getAnswerData(docId)

                }else{
                    Toast.makeText(requireContext(), "belge bulunamadı", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "Bir şeyler ters Gitti..", Toast.LENGTH_SHORT).show()
            }

        }

    }

    //It retrieves all the answers to a question from Firebase.
    private fun getAnswerData(docId: String?){
        val docInCollectRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)

        if(docId != null){
             docInCollectRef.document(docId).collection(FirebaseConstants.COLLECTION_PATH_ANSWERS).get().addOnSuccessListener { querySnapshot ->
                 val context = fragmentContext ?: return@addOnSuccessListener

                 for (document in querySnapshot.documents) {
                     val answerContent = document.getString(FirebaseConstants.FIELD_ANSWER_CONTENT)
                     val answerUsername = document.getString(FirebaseConstants.FIELD_ANSWER_USERNAME)
                     val answerProfileImage = document.getString(FirebaseConstants.FIELD_ANSWER_PROFILE_IMAGE)
                     val answerAddingImage = document.get(FirebaseConstants.FIELD_ANSWER_ADDING_IMAGE) as ArrayList<*>

                     Log.i("AnswerAddingImage", answerAddingImage.toString())

                     val answer = Answer(answerUsername, answerProfileImage, answerContent,answerAddingImage)
                     insideTheQuestionList.add(answer)
                 }
                 insideTheQuestionRecyclerView.layoutManager = LinearLayoutManager(context)
                 insideTheQuestionAdapter = InsideTheQuestionAdapter(insideTheQuestionList)
                 insideTheQuestionRecyclerView.adapter = insideTheQuestionAdapter
                 insideTheQuestionAdapter.submitData(insideTheQuestionList)


             } .addOnFailureListener { e ->
                 val context = fragmentContext ?: return@addOnFailureListener
                 Toast.makeText(context, "Hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
             }
            /*
            docInCollectRef.document(docId).collection(FirebaseConstants.COLLECTION_PATH_ANSWERS).addSnapshotListener{ value, error ->
                val context = fragmentContext ?: return@addSnapshotListener
                if(error != null){
                    Toast.makeText(context, "error bulundu", Toast.LENGTH_SHORT).show()
                }else{
                    if(value != null){
                        val documents = value.documents

                        for(document in documents){
                            val answerContent = document.getString(FirebaseConstants.FIELD_ANSWER_CONTENT)
                            val answerUsername = document.getString(FirebaseConstants.FIELD_ANSWER_USERNAME)
                            val answerProfileImage = document.getString(FirebaseConstants.FIELD_ANSWER_PROFILE_IMAGE)
                            //val answerAddingImage = document.getString(FirebaseConstants.FIELD_ANSWER_ADDING_IMAGE)

                            val answer = Answer(answerUsername,answerProfileImage,answerContent)
                            insideTheQuestionList.add(answer)
                        }

                        insideTheQuestionRecyclerView.layoutManager = LinearLayoutManager(context)
                        insideTheQuestionAdapter = InsideTheQuestionAdapter(insideTheQuestionList)
                        insideTheQuestionRecyclerView.adapter = insideTheQuestionAdapter

                    }else{
                        Toast.makeText(context, "belge bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }

            }

             */

        }

    }



}

