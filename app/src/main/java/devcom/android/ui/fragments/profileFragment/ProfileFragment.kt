package devcom.android.ui.fragments.profileFragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import devcom.android.R
import devcom.android.data.repository.DataStoreRepository
import devcom.android.ui.activity.logins.SignInActivity
import devcom.android.ui.activity.main.MainActivity
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessageFragment
import kotlinx.coroutines.launch
import java.util.*


class ProfileFragment : Fragment() {

    private var authorityStatus:String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null


    private lateinit var bottomNav:BottomNavigationView
    private lateinit var usernameProfileScope:TextView
    private lateinit var profileImageView: ImageView
    private lateinit var addPostImageView: ImageView
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val returnImageView = view.findViewById<ImageView>(R.id.iv_back_profile_to_main)
        addPostImageView = view.findViewById(R.id.iv_add_post)
        usernameProfileScope = view.findViewById(R.id.tv_username)
        profileImageView = view.findViewById(R.id.iv_profile2)
        bottomNav.visibility = View.VISIBLE

        returnImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        dataStoreRepository = DataStoreRepository(requireContext())
        checkAuthority()

        signOutSetOnClickListener()
        addPostSetOnClickListener()

        getData()
        registerLauncher()
        setProfileImageSetOnClickListener()
    }

    private fun addPostSetOnClickListener(){
        addPostImageView.setOnClickListener {
            popUpMenu()
        }
    }
    private fun popUpMenu(){
        val popupMenu = PopupMenu(requireContext(),addPostImageView)
        popupMenu.inflate(R.menu.add_news_menu)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.add_blog ->{
                    val action = ProfileFragmentDirections.actionProfileFragmentToAddBlogFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                    bottomNav.visibility = View.INVISIBLE
                    true
                }
                R.id.add_announcents ->{
                    val action = ProfileFragmentDirections.actionProfileFragmentToAddAnnouncentsFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                    bottomNav.visibility = View.INVISIBLE
                    true
                }
                R.id.add_news ->{
                    val action = ProfileFragmentDirections.actionProfileFragmentToAddNewsFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                    bottomNav.visibility = View.INVISIBLE
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            bottomNav = context.getBottomView()
        }
    }




    private fun checkAuthority(){
        lifecycleScope.launch {
            authorityStatus = dataStoreRepository.getDataFromDataStore()
        }
        if(authorityStatus == "editor"){
            addPostImageView.visibility = View.VISIBLE
        }
    }

    private fun signOutSetOnClickListener(){
        val lineExit = view?.findViewById<LinearLayout>(R.id.line_exit)
        lineExit?.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
    private fun storageImage(){
        //Universal Unique id
        val uuids = UUID.randomUUID()
        val imageName = "$uuids.jpg"

        val reference = storage.reference
        val imageRef = reference.child("profileImages").child(imageName)

        if(selectedPicture != null){
            imageRef.putFile(selectedPicture!!).addOnSuccessListener {
                //download url -> firestore a aktaracaz
                val uploadPicRef = storage.reference.child("profileImages").child(imageName)
                uploadPicRef.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    val collecRef = db.collection(FirebaseConstants.COLLECTION_PATH_USERS)

                    val updates = hashMapOf<String,Any>(
                        "downloadUrl" to downloadUrl
                    )
                    collecRef.whereEqualTo(FirebaseConstants.FIELD_UUID,auth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener {documents ->
                            for(documentt in documents){
                                if(!documentt.contains("donwloadUrl")){
                                    documentt.reference.update(updates)
                                }
                            }
                        }.addOnFailureListener {
                            showToastMessageFragment("Bir şeyler ters gitti, tekrar deneyiniz")
                        }
                }

            }.addOnFailureListener{
                showToastMessageFragment("Bir şeyler ters gitti,lütfen tekrar deneyiniz")
            }
        }
    }
    private fun setProfileImageSetOnClickListener(){
        profileImageView.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }else{
                val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                    storageImage()

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

        db.collection("users").addSnapshotListener{ value,error ->
            if(error != null){
                Toast.makeText(requireContext(), "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(document in documents){

                            val uuid = document.get("uuid") as? String
                            val username = document.get("username") as? String
                            val downloadUrl = document.get("downloadUrl") as? String
                            val authority = document.get("authority") as? String

                            if(uuid == auth.currentUser!!.uid){
                                 usernameProfileScope.text = username

                                if(downloadUrl != null){
                                    Picasso.get().load(downloadUrl).resize(200,200).centerCrop().into(profileImageView)
                                }else{
                                    continue
                                }
                            }
                        }
                    }
                }
            }

        }
    }

}