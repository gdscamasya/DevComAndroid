package devcom.android.ui.activity.logins

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import devcom.android.R
import devcom.android.databinding.ActivityForRegisterWithGoogleBinding
import devcom.android.ui.activity.main.EditorActivity
import devcom.android.ui.activity.main.MainActivity
import devcom.android.users.User
import devcom.android.users.usernameAndAuth
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.navigateToAnotherActivity
import devcom.android.utils.extensions.showToastMessage

class ForRegisterWithGoogleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityForRegisterWithGoogleBinding
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForRegisterWithGoogleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        Toast.makeText(this, auth.currentUser!!.uid, Toast.LENGTH_SHORT).show()

        searchDb()

        registerSetOnClickListener()
    }

    private fun getUsername() : String {
        return "${binding.etpNickname.text}"
    }


    private fun registerSetOnClickListener() {
        binding.btnRegister.setOnClickListener {

                db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                    .document(auth.currentUser!!.uid).update("username", getUsername())
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "kullanıcı adı bilgileriniz oluşturuldu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        "bir şeyler yanlış gitti tekrar deneyiniz",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

    private fun searchDb(){
        db.collection("users").addSnapshotListener{ value,error ->
            if(error != null){
                Toast.makeText(this, "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(document in documents){

                            val uuid = document.get("uuid") as String
                            val nick = document.get("username") as String
                            val authority = document.get("authority") as String

                            if(uuid == auth.currentUser!!.uid.toString()){
                                Toast.makeText(this, "if blokuna girdim", Toast.LENGTH_SHORT).show()
                                if(nick.isNotEmpty()){
                                    Toast.makeText(this, "onCreate fonk çalıştı", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }
                }
            }
        }
    }

}