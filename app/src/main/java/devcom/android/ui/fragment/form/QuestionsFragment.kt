package devcom.android.ui.fragment.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.R
import devcom.android.data.repository.DataStoreRepository
import devcom.android.logic.usecase.CheckLikedQuestions
import devcom.android.logic.usecase.LikedQuestion
import devcom.android.ui.fragment.form.adapter.QuestionRecyclerAdapter
import devcom.android.data.Question
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessageFragment
import devcom.android.viewmodel.QuestionViewModel
import devcom.android.viewmodel.QuestionViewModelFactory


private lateinit var questionList: ArrayList<Question>
private lateinit var questionRecyclerAdapter: QuestionRecyclerAdapter
private lateinit var questionRecyclerView: RecyclerView
private lateinit var questionViewModel: QuestionViewModel


class QuestionsFragment : Fragment() {

    private lateinit var dataStoreRepository: DataStoreRepository
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
        Log.i("PageChange", "QuestionFragment Sayfasına gitti")



        questionList = ArrayList()

        getData()

        val questionViewModelFactory =
            QuestionViewModelFactory(LikedQuestion(db), CheckLikedQuestions(db))
        questionViewModel =
            ViewModelProvider(this, questionViewModelFactory).get(QuestionViewModel::class.java)




        questionRecyclerView = view.findViewById(R.id.rv_question)

        questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        questionRecyclerAdapter =
            QuestionRecyclerAdapter(questionList, object : RecyclerViewItemClickListener {
                override fun onClick(param: Any?) {
                    val action = FormFragmentDirections.actionFormToInsideTheQuestionFragment(param as String?)
                    Navigation.findNavController(requireView()).navigate(action)
                }

            }, object : RecyclerViewItemClickListener {
                override fun onClick(param: Any?) {
                    questionViewModel.likedQuestions(
                        requireView(),
                        requireContext(),
                        questionList,
                        param as Int
                    )
                }

            })
        questionRecyclerView.adapter = questionRecyclerAdapter


    }

    private fun observeLiveData() {
        observeIsLikedQuestions()
        observeCheckLikedQuestion()
    }

    private fun observeIsLikedQuestions() {
        questionViewModel.isLikedQuestion.observe(viewLifecycleOwner) { isNowLikedQuestion ->
            if (isNowLikedQuestion) {
                showToastMessageFragment("başarılı add LikedQuestion FireStore")
            } else {
                showToastMessageFragment("başarısız add LikedQuestion FireStore")

            }

        }
    }

    private fun observeCheckLikedQuestion() {
        questionViewModel.isCheckLikedQuestions.observe(viewLifecycleOwner) { isCheckLikedQuestion ->
            if (isCheckLikedQuestion) {
                showToastMessageFragment("başarılı check LikedQuestion FireStore")
            } else {
                showToastMessageFragment("başarısız check LikedQuestion FireStore")

            }

        }
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

    private fun getData() {

        db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(
                        requireContext(),
                        "beklenmedik bir hata oluştu.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents

                            questionList.clear()

                            for (document in documents) {

                                val docNum = document.id
                                val askingUsername =
                                    document.get(FirebaseConstants.FIELD_QUESTION_USERNAME) as? String
                                val questionContent =
                                    document.get(FirebaseConstants.FIELD_QUESTION_CONTENT) as? String
                                val questionHeader =
                                    document.get(FirebaseConstants.FIELD_QUESTION_HEADER) as? String
                                val questionImage =
                                    document.get(FirebaseConstants.FIELD_QUESTION_IMAGE) as? String
                                val questionTags =
                                    document.get(FirebaseConstants.FIELD_QUESTION_TAGS) as? String
                                val questionProfileImage =
                                    document.get(FirebaseConstants.FILED_QUESTION_PROFILE_IMAGE) as? String
                                val questionPoint =
                                    document.getLong(FirebaseConstants.FILED_QUESTION_POINT)

                                val askingQuestions = Question(
                                    docNum,
                                    questionProfileImage,
                                    askingUsername,
                                    questionContent,
                                    questionHeader,
                                    questionImage,
                                    questionTags,
                                    questionPoint.toString(),
                                    likingViewVisible = false
                                )
                                questionList.add(askingQuestions)

                            }

                            questionRecyclerAdapter.submitData(questionList)
                        }
                    }
                }
            }
    }


}


interface RecyclerViewItemClickListener {
    fun onClick(param: Any?)

}