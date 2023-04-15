package devcom.android.ui.activity.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import devcom.android.databinding.ActivityProfileBinding
import devcom.android.ui.activity.logins.SignInActivity
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessage
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        storage = Firebase.storage

        getData()

        registerLauncher()
        setProfileImageSetOnClickListener(view)
        signOutSetOnClickListener()

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
                            showToastMessage("Bir şeyler ters gitti, tekrar deneyiniz")
                        }
                }

            }.addOnFailureListener{
                showToastMessage("Bir şeyler ters gitti,lütfen tekrar deneyiniz")
            }
        }
    }

    private fun setProfileImageSetOnClickListener(view: View){
        binding.ivProfile2.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(binding.root,"Galeri için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver"){
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentGallery)
            }
        }
    }

    private fun signOutSetOnClickListener(){
        binding.lineExit.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
            this.finish()
        }
    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
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
                val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentGallery)
            }else{
                //permission denied
                Toast.makeText(this, "Bu işlem için izin gerekli!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getRoundedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val radius = bitmap.width.toFloat() / 2
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    private fun getData(){
        db.collection("users").addSnapshotListener{ value,error ->
            if(error != null){
                Toast.makeText(this, "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
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
                                binding.tvUsername.text = username

                                if(downloadUrl != null){
                                    Picasso.get().load(downloadUrl).resize(200,200).centerCrop().into(binding.ivProfile2)
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