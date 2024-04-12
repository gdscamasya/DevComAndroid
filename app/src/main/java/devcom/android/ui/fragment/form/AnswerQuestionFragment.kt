package devcom.android.ui.fragment.form

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import devcom.android.R
import devcom.android.logic.usecase.AnswerQuestionToSaveGlobal
import devcom.android.logic.usecase.AnswerQuestionToSavePersonal
import devcom.android.ui.fragment.form.adapter.AnswerQuestionViewPagerAdapter
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessageFragment
import devcom.android.utils.extensions.touchableScreen
import devcom.android.utils.extensions.unTouchableScreen
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
    private lateinit var imagesViewPager: ViewPager
    private lateinit var pickUpImages: RelativeLayout
    private var profileImageUrl: String? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null
    private lateinit var chooseImageList: ArrayList<Uri>
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
        imagesViewPager = view.findViewById(R.id.viewPager_images)
        pickUpImages = view.findViewById(R.id.rv_pickUpImages)
        chooseImageList = ArrayList<Uri>()

        val answerViewModelFactory = AnswerViewModelFactory(
            AnswerQuestionToSaveGlobal(auth,db,storage),
            AnswerQuestionToSavePersonal(db,storage)
        )

        answerViewModel = ViewModelProvider(this, answerViewModelFactory).get(AnswerViewModel::class.java)

        arguments?.let {
            val docId = AnswerQuestionFragmentArgs.fromBundle(it).docIdInQuestion
            postAnswerBtnSetOnClickListener(docId)
        }

        returnAnswerQuestion.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        registerLauncher()
        pickUpImagesSetOnClickListener()
        observeLiveData()
    }
    private fun pickUpImagesSetOnClickListener(){
        pickUpImages.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }else{
                val intentGallery = Intent()
                intentGallery.setType("image/*")
                intentGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                intentGallery.setAction(Intent.ACTION_GET_CONTENT)
                activityResultLauncher.launch(intentGallery)
            }
        }
    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                val clipData = result.data?.clipData

                if (clipData != null) {
                    chooseImageList.clear()
                    val counts = clipData.itemCount

                    for (count in 0 until counts) {
                        selectedPicture = clipData.getItemAt(count).uri
                        selectedPicture?.let {
                            chooseImageList.add(it)
                        }
                    }

                    setAdapter()
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                //permission Granted
                val intentGallery = Intent()
                intentGallery.setType("images/*")
                intentGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                intentGallery.setAction(Intent.ACTION_GET_CONTENT)
                activityResultLauncher.launch(intentGallery)
            }else{
                //permission denied
                Toast.makeText(requireContext(), "Bu işlem için izin gerekli!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAdapter() {
        imagesViewPager.offscreenPageLimit = 0
        val adapter = AnswerQuestionViewPagerAdapter(requireContext(), chooseImageList)
        imagesViewPager.adapter =  adapter
        adapter.notifyDataSetChanged()
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
                    if(getAnswerContent()){
                        unTouchableScreen(requireView(),R.id.pb_loadingAnswer)
                        answerViewModel.answerQuestionToPersonal(requireContext(),postAnswerContent.text.toString(),chooseImageList)
                        answerViewModel.answerQuestionToGlobal(profileImageUrl,docId,postAnswerContent.text.toString(),chooseImageList)
                        Navigation.findNavController(it).popBackStack()
                        touchableScreen(requireView(),R.id.pb_loadingAnswer)
                    }else{
                        postAnswerContent.setText("Please Fill this area!")
                    }
                }
            }

        }
    }

    private fun getAnswerContent(): Boolean {
        if(postAnswerContent.text.isNotEmpty()){
            return true
        }
        return false
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