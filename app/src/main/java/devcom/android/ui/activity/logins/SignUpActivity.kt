package devcom.android.ui.activity.logins

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.R
import devcom.android.databinding.ActivityRegisterBinding
import devcom.android.logic.use_case.*
import devcom.android.ui.activity.main.MainActivity
import devcom.android.utils.extensions.*
import devcom.android.viewmodel.MainViewModel
import devcom.android.viewmodel.MainViewModelFactory
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    val db = Firebase.firestore
    val fb = LoginManager.getInstance()

    val callbackManager = CallbackManager.Factory.create();
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$"
    private val usersRef = db.collection("users")
    private val permission = listOf<String>("email", "public_profile")

    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val mainViewModelFactory = MainViewModelFactory(
            SignInGoogle(auth, db), SignInFacebook(auth, db),
            SameUsername(auth, db), SignUpEmail(auth, db)
        )
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
        // Initialize Firebase Auth

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        returnSetOnClickListener()
        signUpSetOnClickListener()
        googleLoginSetOnClickListener()
        facebookLoginSetOnClickListener()
        createAccountsListener()

    }

    private fun returnSetOnClickListener() {
        binding.ivBack.setOnClickListener {
            this.finish()
        }
    }
    private fun signUpSetOnClickListener() {
        binding.btnRegister.setOnClickListener {
            if (!validateRegister()) {
                return@setOnClickListener
            }
            unTouchableScreen(R.id.pb_register)
            viewModel.sameUsername(getUsername())
        }
    }
    private fun validateRegister(): Boolean {
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
    private fun createAccountsListener() {
        viewModel.isUsedSameUsername.observe(this){ sameUsername ->
            if(sameUsername){
                binding.etpNickname.error = getString(R.string.used_same_username)
            }else{
                viewModel.signUpEmail(getEmail(), getPassword(), getUsername())
            }
        }

        viewModel.isUsedSameEmail.observe(this) { isUsedEmail ->
            if (isUsedEmail) {
                binding.etpEmail.error = getString(R.string.used_same_email)
            }
        }
        viewModel.isSignUp.observe(this) { isSignUp ->
            if (isSignUp) {
                navigateToAnotherActivity(SignInActivity::class.java)
                showToastMessage(getString(R.string.sign_up_succesfull))
            } else {
                showToastMessage(getString(R.string.something_went_wrong))
            }
        }

        viewModel.isSignedFacebookIn.observe(this) { isSignedFacebookIn ->
            if (isSignedFacebookIn) {
                navigateToAnotherActivity(MainActivity::class.java)
                this.finish()
            } else {
                showToastMessage(getString(R.string.something_went_wrong))
            }
        }
        touchableScreen(R.id.pb_register)
    }

    private fun facebookLoginSetOnClickListener() {
        binding.ivFacebook.setOnClickListener {
            fb.logOut()
            fb.logInWithReadPermissions(this, permission)
            fb.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    unTouchableScreen(R.id.pb_register)
                    viewModel.signInFacebook(loginResult.accessToken,getEmail())
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                    showToastMessage(getString(R.string.something_went_wrong))
                }

            })
        }
    }

    private fun googleLoginSetOnClickListener() {
        binding.ivGoogle.setOnClickListener {
            unTouchableScreen(R.id.pb_sign)
            googleSignInClient.signOut()
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun handleSignInResult(accountTask: Task<GoogleSignInAccount>) {
        try {
            val account = accountTask.getResult(ApiException::class.java)
            auth.fetchSignInMethodsForEmail(account.email.toString()).addOnSuccessListener {
                if (it.signInMethods!!.size > 0 && (it.signInMethods!![0].equals("password") || it.signInMethods!![0].equals("facebook.com")))
                {
                    showToastMessage(getString(R.string.used_same_email))
                    touchableScreen(R.id.pb_register)
                } else {
                    viewModel.signInGoogle(account)
                }
            }

        } catch (e: Exception) {
            showToastMessage(getString(R.string.something_went_wrong))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(accountTask)
        }

        callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun getPassword(): String {
        return "${binding.etpPassword.text}"
    }

    private fun getEmail(): String {
        return "${binding.etpEmail.text}"
    }

    private fun getUsername(): String {
        return "${binding.etpNickname.text}"
    }


}




