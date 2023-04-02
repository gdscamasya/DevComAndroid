package devcom.android.ui.activity.logins

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import devcom.android.R
import devcom.android.databinding.ActivityRegisterBinding
import devcom.android.utils.extensions.navigateToAnotherActivity
import devcom.android.utils.extensions.showToastMessage
import devcom.android.ui.activity.main.MainActivity
import devcom.android.users.User
import devcom.android.users.uuid
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.gone
import devcom.android.utils.extensions.visible
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient :GoogleSignInClient
    val db = FirebaseFirestore.getInstance()
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$"
    private val usersRef = db.collection("users")

    private companion object{
        private const val  RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Initialize Firebase Auth
        auth = Firebase.auth

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)


        returnSetOnClickListener()
        registerSetOnClickListener()
        googleLoginSetOnClickListener()


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
            googleSignInClient.signOut()
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(accountTask)
        }
    }

    private fun handleSignInResult(accountTask: Task<GoogleSignInAccount>) {
        try {
            val account = accountTask.getResult(ApiException::class.java)
            auth.fetchSignInMethodsForEmail(account.email.toString()).addOnSuccessListener{
                if(it.signInMethods!!.size > 0 && it.signInMethods!![0].equals("password")){
                    showToastMessage("Bu email adresi ile farklı bir yöntemle kayıt olunmuş")
                }else{
                    firebaseAuthWithGoogleAccount(account)
                }
            }

        }catch (e:Exception){
            Toast.makeText(this, "Bir şeyler ters gitti, tekrar deneyiniz", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken,null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            //login success
            val user = auth.currentUser

            if(it.additionalUserInfo!!.isNewUser){
                db.collection(FirebaseConstants.COLLECTION_PATH_USERS).add(uuid(user!!.uid))
                    .addOnSuccessListener {
                        navigateToAnotherActivity(ForRegisterWithGoogleActivity::class.java)
                        showToastMessage("başarılı bir şekilde kayıt oldunuz")
                    }.addOnFailureListener {
                        showToastMessage(getString(R.string.something_went_wrong))
                    }
                // Toast.makeText(this, "yeni kullanıcı oluşturuldu", Toast.LENGTH_SHORT).show()
            }else{
                //Toast.makeText(this, "zaten böyle bir kullanıcı vardı", Toast.LENGTH_SHORT).show()
                navigateToAnotherActivity(ForRegisterWithGoogleActivity::class.java)
            }
        }.addOnFailureListener {
            showToastMessage("bir şeyler yanlış gitti...")
        }
    }

    private fun validateRegister() : Boolean {
        val nick = binding.etpNickname.text.toString()
        val email = binding.etpEmail.text.toString()
        val password = binding.etpPassword.text.toString()

        return when {
            nick.isEmpty() -> {
                binding.etpNickname.error = getString(R.string.please_enter_username)
                false
            }
            email.isEmpty() || !email.matches(emailPattern.toRegex()) -> {
                binding.etpEmail.error = getString(R.string.please_enter_email_username)
                false
            }
            password.isEmpty() -> {
                binding.etpPassword.error = getString(R.string.please_enter_password_username)
                false
            }
            !password.matches(passwordPattern.toRegex()) -> {
                binding.etpPassword.error = getString(R.string.password_rules)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun getPassword(): String{
        return "${binding.etpPassword.text}"
    }

    private fun getEmail() :String{
        return "${binding.etpEmail.text}"
    }

    private fun getUsername() : String {
        return "${binding.etpNickname.text}"
    }

    private fun checkAndCreateUsername() {
        unTouchableScreen()

        usersRef.whereEqualTo(FirebaseConstants.FIELD_USERNAME, getUsername()).get().addOnSuccessListener { querySnapshot ->
            if(querySnapshot.size() > 0){
                binding.etpNickname.error = "Bu kullanıcı adı daha önce kullanılmış!"
                touchableScreen()
            }else{
                auth.fetchSignInMethodsForEmail(getEmail()).addOnSuccessListener {
                    Log.i("listys",it.signInMethods.toString())
                    if(it.signInMethods!!.size > 0){
                        binding.etpEmail.error="Bu email adresi daha önce kullanılmış"
                        touchableScreen()
                    }else{
                        auth.createUserWithEmailAndPassword(getEmail(), getPassword()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                db.collection(FirebaseConstants.COLLECTION_PATH_USERS).add(User(getUsername(), auth.currentUser!!.uid, "User"))
                                    .addOnSuccessListener {
                                        navigateToAnotherActivity(SignInActivity::class.java)
                                        showToastMessage("başarılı bir şekilde kayıt oldunuz")
                                    }.addOnFailureListener {
                                        showToastMessage(getString(R.string.something_went_wrong))
                                    }
                            } else {
                                Toast.makeText(this,getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show()
                            }
                            touchableScreen()
                        }

                    }

                }

            }
        }
    }


    private fun registerSetOnClickListener() {
        binding.btnRegister.setOnClickListener {
            if (!validateRegister()) {
                return@setOnClickListener
            }
            checkAndCreateUsername()
        }
    }

    private fun unTouchableScreen(){
        binding.pbRegister.visible()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
    private fun touchableScreen(){
        binding.pbRegister.gone()
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


}


