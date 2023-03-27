package devcom.android.ui.activity.logins

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.databinding.ActivityRegisterBinding
import devcom.android.extensions.navigateToAnotherActivity
import devcom.android.users.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$"
    val nicknameArray = ArrayList<String>()
    val usersRef = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Initialize Firebase Auth
        auth = Firebase.auth

        returnSetOnClickListener()
        registerSetOnClickListener()

    }

    private fun returnSetOnClickListener() {
        binding.ivBack.setOnClickListener {
            this.finish()
        }
    }

    private fun facebookLoginSetOnClickListener() {
        binding.ivFacebook.setOnClickListener {

        }
    }

    private fun googleLoginSetOnClickListener() {
        binding.ivGoogle.setOnClickListener {

        }
    }


    private fun registerSetOnClickListener() {
        binding.btnRegister.setOnClickListener {
            val nick = binding.etpNickname.text.toString()
            val email = binding.etpEmail.text.toString()
            val password = binding.etpPassword.text.toString()

            if (nick.isEmpty() || email.isEmpty() || password.isEmpty()) {
                if (nick.isEmpty()) {
                    binding.etpNickname.error = "Lütfen kullanıcı adınızı giriniz!"
                }
                if (email.isEmpty()) {
                    binding.etpEmail.error = "Lütfen e-mail adresinizi giriniz!"
                }
                if (password.isEmpty()) {
                    binding.etpPassword.error = "Lütfen şifrenizi girinizi!"
                }
            }else if(nick.isNotEmpty()){
                binding.pbRegister.visibility = View.VISIBLE
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                usersRef.whereEqualTo("nickname", nick).get().addOnSuccessListener {
                    binding.etpNickname.error = "Bu kullanıcı adı daha önce kullanılmış!"
                    binding.pbRegister.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }else if (!email.matches(emailPattern.toRegex())) {
                binding.etpEmail.error = "Lütfen geçerli bir email adresi giriniz!"
            } else if (!password.matches(passwordPattern.toRegex())) {
                binding.etpPassword.error ="en az 1 sayı\nen az 1 küçük harf\nen az 1 büyük harf\nen az 6 karakter"
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        db.collection("users").add(User(nick, auth.currentUser!!.uid, "User"))
                            .addOnSuccessListener {
                                navigateToAnotherActivity(SignInActivity::class.java)
                                binding.pbRegister.visibility = View.INVISIBLE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }.addOnFailureListener {
                            Toast.makeText(this,"Bir şeyler yanlış gitti, tekrar deneyiniz.",Toast.LENGTH_SHORT).show()
                                binding.pbRegister.visibility = View.INVISIBLE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }
                    } else {
                        Toast.makeText(this,"Bir şeyler yanlış gitti, tekrar deneyiniz.",Toast.LENGTH_SHORT).show()
                        binding.pbRegister.visibility = View.INVISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        }

    }

}


