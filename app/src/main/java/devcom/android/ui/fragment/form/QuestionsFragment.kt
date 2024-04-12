package devcom.android.ui.fragment.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import kotlinx.coroutines.launch

var questionList: ArrayList<Question> = ArrayList()
lateinit var questionRecyclerAdapter: QuestionRecyclerAdapter

class QuestionsFragment : Fragment() {

    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var swipeRefreshQuestionLayout: SwipeRefreshLayout

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

        val questionViewModelFactory =
            QuestionViewModelFactory(LikedQuestion(db), CheckLikedQuestions(db))
        questionViewModel =
            ViewModelProvider(this, questionViewModelFactory).get(QuestionViewModel::class.java)

        getData()

        swipeRefreshQuestionLayout = view.findViewById(R.id.swipeRefreshQuestionLayout)
        questionRecyclerView = view.findViewById(R.id.rv_question)

        questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        questionRecyclerAdapter =
            QuestionRecyclerAdapter(object : RecyclerViewItemClickListener {
                //if Form Item navigate to InsideTheQuestionFragment
                override fun onClick(param: Any?) {
                    val action =
                        FormFragmentDirections.actionFormToInsideTheQuestionFragment(param as String?)
                    Navigation.findNavController(requireView()).navigate(action)
                }

            }, object : RecyclerViewItemClickListener {
                //if Click Like Button update QuestionRecyclerAdapter
                override fun onClick(param: Any?) {
                    questionViewModel.likedQuestions(
                        requireView(),
                        requireContext(),
                        questionList,
                        param as Int,
                        "QuestionList"
                    )
                }

            })

        questionRecyclerView.adapter = questionRecyclerAdapter

        observeLiveData()
        refreshSetOnListener()
    }

    private fun observeLiveData() {
        questionViewModel.isLikedQuestion.observe(viewLifecycleOwner, Observer { LikedQuestion ->
            LikedQuestion?.let {
                if (LikedQuestion) {
                    updateUI()
                }
            }
        })
    }

    private fun updateUI() {
        getData()
    }


    private fun refreshSetOnListener() {
        swipeRefreshQuestionLayout.setOnRefreshListener {
            getData()
            swipeRefreshQuestionLayout.isRefreshing = false
        }
    }

    private fun getData() {
        //Get Questions data with not limitation

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

                                val pending =
                                    document.get(FirebaseConstants.FILED_QUESTION_PENDING) as? Boolean
                                Toast.makeText(
                                    requireContext(),
                                    "Pending = $pending",
                                    Toast.LENGTH_SHORT
                                ).show()

                                if (pending == false) {
                                    continue
                                } else {
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

                            }

                            questionViewModel.checkLikedQuestions(
                                requireView(), requireContext(),
                                questionList, "QuestionList"
                            )


                        }
                    }
                }
            }
    }

    fun refresh() {
        //When slide ViewPager2 Refresh contents on page
        //getData()

    }

}

interface RecyclerViewItemClickListener {
    fun onClick(param: Any?)

}