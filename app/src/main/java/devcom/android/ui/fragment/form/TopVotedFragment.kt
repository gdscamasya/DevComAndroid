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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.R
import devcom.android.ui.fragment.form.adapter.TopQuestionAdapter
import devcom.android.data.Question
import devcom.android.logic.usecase.CheckLikedQuestions
import devcom.android.logic.usecase.LikedQuestion
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.viewmodel.QuestionViewModel
import devcom.android.viewmodel.QuestionViewModelFactory


var topQuestionList: ArrayList<Question> = ArrayList()
lateinit var topQuestionRecyclerAdapter: TopQuestionAdapter


class TopVotedFragment : Fragment() {

    private lateinit var topQuestionRecycleView: RecyclerView
    private lateinit var topQuestionViewModel: QuestionViewModel
    private lateinit var swipeRefreshTopLayout: SwipeRefreshLayout

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_voted, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questionViewModelFactory =
            QuestionViewModelFactory(LikedQuestion(db), CheckLikedQuestions(db))
        topQuestionViewModel =
            ViewModelProvider(this, questionViewModelFactory).get(QuestionViewModel::class.java)

        getData()


        swipeRefreshTopLayout = view.findViewById(R.id.swipeRefreshTopLayout)
        topQuestionRecycleView = view.findViewById(R.id.topVotedRecycler)

        topQuestionRecycleView.layoutManager = LinearLayoutManager(requireContext())
        topQuestionRecyclerAdapter = TopQuestionAdapter(object : TopRecyclerViewItemClickListener {
            override fun onClick(param: Any?) {
                //if Form Item navigate to InsideTheQuestionFragment
                val action =
                    FormFragmentDirections.actionFormToInsideTheQuestionFragment(param as String?)
                Navigation.findNavController(requireView()).navigate(action)
            }

        }, object : TopRecyclerViewItemClickListener {
            override fun onClick(param: Any?) {
                //if Click Like Button update TopQuestionRecyclerAdapter
                topQuestionViewModel.likedQuestions(
                    requireView(),
                    requireContext(),
                    topQuestionList,
                    param as Int,
                    "TopQuestionList"
                )
            }
        })
        topQuestionRecycleView.adapter = topQuestionRecyclerAdapter

        observeLiveData()
        setOnRefreshListener()
    }

    private fun observeLiveData(){
        topQuestionViewModel.isLikedQuestion.observe(viewLifecycleOwner, Observer {LikedQuestion ->
            LikedQuestion?.let {
                if(LikedQuestion){
                    updateUI()
                }
            }
        })
    }
    private fun updateUI() {
        getData()
    }
    private fun setOnRefreshListener() {
        //Refresh Data and change View
        swipeRefreshTopLayout.setOnRefreshListener {
            getData()
            swipeRefreshTopLayout.isRefreshing = false
        }
    }

    private fun getData() {
        //Get Questions data with limited 10 question
        db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)
            .orderBy(FirebaseConstants.FILED_QUESTION_POINT, Query.Direction.DESCENDING).limit(10)
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

                            topQuestionList.clear()

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
                                    val docNUm = document.id
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
                                        document.get(FirebaseConstants.FILED_QUESTION_PROFILE_IMAGE) as? String?
                                    val questionPoint =
                                        document.getLong(FirebaseConstants.FILED_QUESTION_POINT)

                                    val askingQuestions = Question(
                                        docNUm,
                                        questionProfileImage,
                                        askingUsername,
                                        questionContent,
                                        questionHeader,
                                        questionImage,
                                        questionTags,
                                        questionPoint.toString(),
                                        likingViewVisible = false
                                    )
                                    topQuestionList.add(askingQuestions)

                                }

                                topQuestionViewModel.checkLikedQuestions(
                                    requireView(), requireContext(),
                                    topQuestionList, "TopQuestionList"
                                )

                            }

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

interface TopRecyclerViewItemClickListener {
    fun onClick(param: Any?)

}