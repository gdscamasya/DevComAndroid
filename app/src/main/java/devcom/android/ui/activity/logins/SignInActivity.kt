package devcom.android.ui.activity.logins

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.databinding.ActivitySignInBinding
import devcom.android.ui.activity.main.EditorActivity
import devcom.android.ui.activity.main.MainActivity
import devcom.android.utils.extensions.*

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Initialize Firebase Auth
        auth = Firebase.auth

        forgetPasswordSetOnClickListener()
        registerSetOnClickListener()
        signInSetOnClickListener()


    }
    /*
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload()
        }
    }

     */
    /*
    private fun reload() {
        navigateToAnotherActivity(MainActivity::class.java)
    }

     */

    private fun forgetPasswordSetOnClickListener(){
        binding.tvForgetPassaword.setOnClickListener {
            navigateToAnotherActivity(PasswordResetActivity::class.java)
        }
    }
    private fun registerSetOnClickListener(){
        binding.btnRegister.setOnClickListener {
            navigateToAnotherActivity(RegisterActivity::class.java)

        }
    }
    private fun facebookLoginSetOnClickListener(){
        binding.ivFacebook.setOnClickListener {

        }
    }
    private fun googleLoginSetOnClickListener(){
        binding.ivGoogle.setOnClickListener {

        }
    }

    private fun signInSetOnClickListener(){
        binding.btnSignIn.setOnClickListener {
            val nickOrEmail = binding.etpNickname.text.toString()
            val password = binding.etpPassword.text.toString()

            if(nickOrEmail.isEmpty() || password.isEmpty()){
                if(nickOrEmail.isEmpty()){
                    binding.etpNickname.error = "Kullanıcı adı veya Email adresinizi giriniz."
                }
                if(password.isEmpty()){
                    binding.etpPassword.error = "şifrenizi giriniz."
                }
            }else{
                auth.signInWithEmailAndPassword(nickOrEmail,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        binding.pbSign.visible()
                        unTouchableScreen()
                        getDataBase()
                    }else{
                        Toast.makeText(this, "Bir şeyler ters gitti, tekrar deneyiniz.", Toast.LENGTH_SHORT).show()
                        touchableScreen()
                    }

                }
            }

        }
    }

private fun getDataBase(){
    db.collection("users").addSnapshotListener{ value,error ->
        if(error != null){
            Toast.makeText(this, "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
        }else{
            if(value != null){
                if(!value.isEmpty){
                    val documents = value.documents

                    for(document in documents){
                        val uuid = document.get("uuid") as String
                        val authority = document.get("authority") as String

                        if(uuid == auth.currentUser!!.uid.toString()){
                            if(authority == "User"){
                                navigateToAnotherActivity(MainActivity::class.java)
                            }
                            if(authority == "Editor"){
                                navigateToAnotherActivity(EditorActivity::class.java)
                            }
                        }

                    }
                }
            }
        }
        binding.pbSign.gone()
        touchableScreen()
    }
}

}