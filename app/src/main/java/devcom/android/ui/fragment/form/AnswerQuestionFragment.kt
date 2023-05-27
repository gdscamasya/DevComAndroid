package devcom.android.ui.fragment.form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import devcom.android.R
import devcom.android.logic.usecase.AnswerQuestionToSaveGlobal
import devcom.android.logic.usecase.AnswerQuestionToSavePersonal
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessageFragment
import devcom.android.viewmodel.AnswerViewModel
import devcom.android.viewmodel.AnswerViewModelFactory
import kotlinx.coroutines.launch


class AnswerQuestionFragment : Fragment() {

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var answerViewModel: AnswerViewModel
    private lateinit var postAnswerBtn: Button
    private lateinit var postAnswerContent: EditText
    private lateinit var returnAnswerQuestion: ImageView
    private var profileImageUrl: String? = null

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_answer_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        storage = Firebase.storage
        getData()

        postAnswerBtn = view.findViewById(R.id.btn_post_question_answer)
        postAnswerContent = view.findViewById(R.id.etp_answer_content)
        returnAnswerQuestion = view.findViewById(R.id.iv_return_answer_question)



        val answerViewModelFactory = AnswerViewModelFactory(
            AnswerQuestionToSaveGlobal(auth,db,storage),
            AnswerQuestionToSavePersonal(db)
        )

        answerViewModel = ViewModelProvider(this, answerViewModelFactory).get(AnswerViewModel::class.java)

        arguments?.let {
            val docId = AnswerQuestionFragmentArgs.fromBundle(it).docIdInQuestion
            postAnswerBtnSetOnClickListener(docId)
        }

        returnAnswerQuestion.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }


        observeLiveData()
    }


    private fun observeLiveData(){
        observeAnswerQuestionToPersonal()
        observeAnswerQuestionToGlobal()
    }

    private fun observeAnswerQuestionToPersonal(){

        answerViewModel.isAnswerQuestionPersonal.observe(viewLifecycleOwner){ isAnswerPersonal ->
            if(isAnswerPersonal){
                showToastMessageFragment("successful")
            }else{
                showToastMessageFragment("cant do this")
            }
        }

    }

    private fun observeAnswerQuestionToGlobal(){
        answerViewModel.isAnswerQuestionGlobal.observe(viewLifecycleOwner){ isAnswerGlobal ->
            if(isAnswerGlobal){
                showToastMessageFragment("successful Global")
            }else{
                showToastMessageFragment("cant do this Global")
            }

        }


    }


    private fun postAnswerBtnSetOnClickListener(docId: String?) {
        postAnswerBtn.setOnClickListener {

            if(docId != null){
                lifecycleScope.launch {
                    answerViewModel.answerQuestionToGlobal(profileImageUrl,docId,getAnswerContent())
                    answerViewModel.answerQuestionToPersonal(requireContext(),getAnswerContent())
                    Navigation.findNavController(it).popBackStack()
                }
            }

        }
    }

    private fun getAnswerContent(): String {
        return postAnswerContent.text.toString()
    }


    private fun getData(){
        db.collection(FirebaseConstants.COLLECTION_PATH_USERS).addSnapshotListener{ value,error ->
            if(error != null){
                Toast.makeText(requireContext(), "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(document in documents){

                            val uuid = document.get("uuid") as? String
                            val downloadUrl = document.get("downloadUrl") as? String

                            if(uuid == auth.currentUser!!.uid){

                                if (downloadUrl != null) {
                                    profileImageUrl = downloadUrl
                                }

                            }
                        }
                    }
                }
            }

        }
    }
}