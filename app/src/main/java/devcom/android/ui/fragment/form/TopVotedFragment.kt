package devcom.android.ui.fragment.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.R
import devcom.android.data.repository.DataStoreRepository
import devcom.android.ui.fragment.form.adapter.TopQuestionAdapter
import devcom.android.data.Question
import devcom.android.logic.usecase.CheckLikedQuestions
import devcom.android.logic.usecase.LikedQuestion
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.viewmodel.QuestionViewModel
import devcom.android.viewmodel.QuestionViewModelFactory
import kotlinx.coroutines.launch


lateinit var topQuestionList: ArrayList<Question>
private lateinit var topVotedRecycleView: RecyclerView
lateinit var topQuestionAdapter: TopQuestionAdapter
private lateinit var likedQuestionsTop: ArrayList<String?>
lateinit var likedIndexQuestionsTopVoted: ArrayList<Int?>
private lateinit var topQuestionViewModel: QuestionViewModel

class TopVotedFragment : Fragment() {

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
        Log.i("PageChange", "TopVoted Sayfasına gitti")


        likedIndexQuestionsTopVoted = ArrayList()
        likedQuestionsTop = ArrayList()
        topQuestionList = ArrayList()

        getData()

        val questionViewModelFactory = QuestionViewModelFactory(LikedQuestion(db), CheckLikedQuestions(db))
        topQuestionViewModel = ViewModelProvider(this, questionViewModelFactory).get(QuestionViewModel::class.java)

        swipeRefreshTopLayout = view.findViewById(R.id.swipeRefreshTopLayout)
        topVotedRecycleView = view.findViewById(R.id.topVotedRecycler)

        topVotedRecycleView.layoutManager = LinearLayoutManager(requireContext())
        topQuestionAdapter = TopQuestionAdapter(topQuestionList, object : TopRecyclerViewItemClickListener{
            override fun onClick(param: Any?) {
                val action = FormFragmentDirections.actionFormToInsideTheQuestionFragment(param as String?)
                Navigation.findNavController(requireView()).navigate(action)
            }

        },object : TopRecyclerViewItemClickListener{
            override fun onClick(param: Any?) {
                topQuestionViewModel.likedQuestions(
                    requireView(),
                    requireContext(),
                    topQuestionList,
                    param as Int,
                    "TopQuestions"
                )
            }

        })
        topVotedRecycleView.adapter = topQuestionAdapter

        topQuestionViewModel.checkLikedQuestions(
            requireView(),
            requireContext(),
            topQuestionList,
            "TopQuestions"
        )

        setOnRefreshListener()
    }

    private fun setOnRefreshListener(){
        swipeRefreshTopLayout.setOnRefreshListener {
            getData()
            topQuestionAdapter.submitDataTopVoted(topQuestionList)
            swipeRefreshTopLayout.isRefreshing = false
        }
    }
    fun getData() {
        Log.i("PageChange", "TopVoted Sayfasına gitti GetData()")

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
                            topQuestionAdapter.submitDataTopVoted(topQuestionList)
                        }

                    }
                }

            }
    }

}

interface TopRecyclerViewItemClickListener {
    fun onClick(param: Any?)

}