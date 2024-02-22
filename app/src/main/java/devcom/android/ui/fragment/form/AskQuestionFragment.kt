package devcom.android.ui.fragment.form

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import devcom.android.R
import devcom.android.logic.usecase.*
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessageFragment
import devcom.android.viewmodel.FormViewModel
import devcom.android.viewmodel.FormViewModelFactory
import kotlinx.coroutines.launch


class AskQuestionFragment : Fragment() {

    private lateinit var formviewModel: FormViewModel
    private lateinit var returnForm:ImageView
    private lateinit var postQuestionBtn:Button
    private lateinit var editTextContent:EditText
    private lateinit var editTextHeader:EditText
    private lateinit var addImageView: ImageView
    private lateinit var spinner:Spinner
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var profileImageUrl: String? = null

    var selectedPicture: Uri? = null
    val db = Firebase.firestore

    var itemSelected : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_ask_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()

        addImageView = view.findViewById(R.id.iv_add_image)
        spinner = view.findViewById(R.id.spinner)
        editTextContent = view.findViewById(R.id.etp_question_content)
        editTextHeader = view.findViewById(R.id.etp_question_header)
        postQuestionBtn = view.findViewById(R.id.btn_post_question)
        returnForm = view.findViewById(R.id.iv_return_ask_question)

        returnForm.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        val options = arrayOf("Android", "IOS", "Kotlin","Java","C++","C#","WinForms","Web")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                itemSelected = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Lütfen bir seçim yapın", Toast.LENGTH_SHORT).show()
            }
        }

        storage = Firebase.storage
        auth = Firebase.auth
        val formViewModelFactory = FormViewModelFactory(AskQuestionToPersonalSave(auth,db),
            AskQuestionsToSaveGlobal(auth,db,storage)
        )
        formviewModel = ViewModelProvider(this, formViewModelFactory).get(FormViewModel::class.java)


        registerLauncher()
        setAddImageViewSetOnClickListener()
        postQuestionBtnSetOnClickListener()
        observeLiveData()

    }

    private fun observeLiveData(){
        observeAskQuestionToPersonalSave()
        observeAskQuestionToGlobalSave()
    }

    private fun observeAskQuestionToPersonalSave(){

        formviewModel.isAskQuestionPersonal.observe(viewLifecycleOwner){ isAskQuestionPersonal ->
            if(isAskQuestionPersonal){
                showToastMessageFragment("successful")
            }else{
                showToastMessageFragment("cant do this")
            }
        }

    }

    private fun observeAskQuestionToGlobalSave(){
        formviewModel.isAskQuestion.observe(viewLifecycleOwner){ isAskQuestion ->
            if(isAskQuestion){
                showToastMessageFragment("successful Global")
            }else{
                showToastMessageFragment("cant do this Global")
            }

        }
    }


    private fun postQuestionBtnSetOnClickListener(){
        postQuestionBtn.setOnClickListener {
            lifecycleScope.launch {
                formviewModel.askQuestionToSaveGlobal(profileImageUrl,getEditTextContent(),editTextHeader(),itemSelected,selectedPicture,false)
                formviewModel.askQuestionToPersonal(requireContext(),getEditTextContent(),editTextHeader(),false)
                Navigation.findNavController(it).popBackStack()
            }
        }
    }


    private fun getEditTextContent():String{
        return editTextContent.text.toString()
    }
    private fun editTextHeader():String{
        return editTextHeader.text.toString()
    }


    private fun setAddImageViewSetOnClickListener(){
        addImageView.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }else{
                val intentGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentGallery)
            }
        }
    }


    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data//URI alabilyoruz nullable olabilir ama
                if(intentFromResult != null){
                    selectedPicture = intentFromResult.data
                    Picasso.get().load(selectedPicture).resize(300,250).centerCrop().into(object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            val drawable = BitmapDrawable(resources, bitmap)
                            editTextContent.setCompoundDrawablesRelativeWithIntrinsicBounds(null,drawable,null,null)

                            // Burada drawable değişkeni, dışarıdan yüklenen resmin Drawable nesnesidir
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            // Resim yükleme hatası durumunda yapılacak işlemler
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            // Resim yüklenirken yapılacak işlemler
                        }
                    })
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                //permission Granted
                val intentGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentGallery)
            }else{
                //permission denied
                Toast.makeText(requireContext(), "Bu işlem için izin gerekli!", Toast.LENGTH_SHORT).show()
            }
        }

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