package devcom.android.ui.activity.logins

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
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
import devcom.android.data.repository.DataStoreRepository
import devcom.android.databinding.ActivitySignInBinding
import devcom.android.logic.usecase.*
import devcom.android.ui.activity.main.MainActivity
import devcom.android.utils.Resource
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.*
import devcom.android.viewmodel.MainViewModel
import devcom.android.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch
import kotlin.Exception


class SignInActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var dataStoreRepository: DataStoreRepository


    val db = Firebase.firestore

    private val fb = LoginManager.getInstance()
    private val callbackManager = CallbackManager.Factory.create();
    private val permission = listOf<String>("email", "public_profile")

    private companion object {
        private const val RC_SIGN_IN = 1000
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        auth = Firebase.auth
        dataStoreRepository = DataStoreRepository(this@SignInActivity)
        val mainViewModelFactory = MainViewModelFactory(
            SignInGoogle(auth, db), SignInFacebook(auth, db),
            CheckUsername(auth, db), SignUpEmail(auth, db)
        )
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
        // Initialize Firebase Auth


        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)


        forgetPasswordSetOnClickListener()
        registerSetOnClickListener()
        signInSetOnClickListener()
        googleLoginSetOnClickListener()
        facebookLoginSetOnClickListener()
        observeReceived()

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

    private fun reload() {
        navigateToAnotherActivity(MainActivity::class.java)
    }

     */



    private fun observeReceived() {

        viewModel.isExistsEmailFacebook.observe(this) { ExistsEmailFacebook ->
            showSnackBarToMessage(binding.root, ExistsEmailFacebook)
            touchableScreen(R.id.pb_sign)
        }

        viewModel.isSignInFacebook.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    getDataBase()
                    navigateToAnotherActivity(MainActivity::class.java)
                    this.finish()
                }

                is Resource.Error -> {
                    showSnackBarToMessage(binding.root, getString(R.string.something_went_wrong))
                    touchableScreen(R.id.pb_sign)
                }
            }
        }

        viewModel.isSignInGoogle.observe(this) { isSignedGoogleIn ->
            if (isSignedGoogleIn) {
                getDataBase()
                navigateToAnotherActivity(MainActivity::class.java)
                touchableScreen(R.id.pb_sign)
                this.finish()
            } else {
                showSnackBarToMessage(binding.root, getString(R.string.something_went_wrong))
                touchableScreen(R.id.pb_sign)
            }
        }

    }

    private fun forgetPasswordSetOnClickListener() {
        binding.tvForgetPassaword.setOnClickListener {
            navigateToAnotherActivity(UpdatePasswordActivity::class.java)
        }
    }

    private fun registerSetOnClickListener() {
        binding.btnRegister.setOnClickListener {
            navigateToAnotherActivity(SignUpActivity::class.java)
        }
    }

    private fun validateRegister(): Boolean {
        val nick = binding.etpUsername.text.toString()
        val password = binding.etpPassword.text.toString()

        return when {
            nick.isEmpty() -> {
                binding.etpUsername.error = getString(R.string.get_email)
                false
            }

            password.isEmpty() -> {
                binding.etpPassword.error = getString(R.string.please_enter_password_username)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun signInSetOnClickListener() {
        binding.btnSignIn.setOnClickListener {
            if (!validateRegister()) {
                return@setOnClickListener
            }
            unTouchableScreen(R.id.pb_sign)
            signIn()
        }
    }

    private fun getEmail(): String {
        return "${binding.etpUsername.text}"
    }

    private fun getPassword(): String {
        return "${binding.etpPassword.text}"
    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(getEmail(), getPassword()).addOnCompleteListener {
            if (it.isSuccessful) {
                getDataBase()
                navigateToAnotherActivity(MainActivity::class.java)
                this.finish()
            } else {
                Log.i("erroer", it.exception!!.message.toString())
                if (it.exception!!.message.equals(getString(R.string.error_login_disable))) {
                    showSnackBarToMessage(
                        binding.root,
                        getString(R.string.error_login_disable_translate)
                    )
                } else if (it.exception!!.message.equals(getString(R.string.error_login_password))) {
                    showSnackBarToMessage(
                        binding.root,
                        getString(R.string.error_login_password_translate)
                    )
                } else if (it.exception!!.message.equals(getString(R.string.error_login_email))) {
                    showSnackBarToMessage(
                        binding.root,
                        getString(R.string.error_login_email_translate)
                    )
                } else {
                    showSnackBarToMessage(binding.root, getString(R.string.something_went_wrong))
                }
                touchableScreen(R.id.pb_sign)
            }
        }
    }

    private fun facebookLoginSetOnClickListener() {
        binding.ivFacebook.setOnClickListener {
            fb.logOut()
            fb.logInWithReadPermissions(this, permission)
            fb.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    unTouchableScreen(R.id.pb_sign)
                    viewModel.signInFacebook(result.accessToken)

                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {
                    showSnackBarToMessage(binding.root, getString(R.string.something_went_wrong))
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

    private fun updateUI(account: GoogleSignInAccount) {
        try {
            Log.i("statusAccount", account.email!!)
            val email = account.email

            if (email != null) {
                auth.fetchSignInMethodsForEmail(email).addOnSuccessListener {
                    if (it.signInMethods!!.size > 0 && (it.signInMethods!![0].equals("password") || it.signInMethods!![0].equals(
                            "facebook.com"
                        ))
                    ) {
                        showSnackBarToMessage(binding.root, getString(R.string.used_same_email))
                        touchableScreen(R.id.pb_sign)
                    } else {
                        viewModel.signInGoogle(account)
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            showSnackBarToMessage(binding.root, "Bir şeyler ters gitti..")
            touchableScreen(R.id.pb_sign)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)

            if (accountTask.isSuccessful) {
                val getAccount = accountTask.getResult(ApiException::class.java)

                if (getAccount != null) {
                    updateUI(getAccount)
                }

            } else {
                showSnackBarToMessage(binding.root, "Bir şeyler ters gitti..-OnActivityResult")
                touchableScreen(R.id.pb_sign)
            }


        }

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun getDataBase() {
        db.collection(FirebaseConstants.COLLECTION_PATH_USERS).addSnapshotListener { value, error ->
            if (error != null) {
                showSnackBarToMessage(binding.root, getString(R.string.something_went_wrong))
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents

                        for (document in documents) {

                            val uuid = document.get(FirebaseConstants.FIELD_UUID) as? String
                            val authority =
                                document.get(FirebaseConstants.FIELD_AUTHORITY) as? String

                            if (uuid == auth.currentUser!!.uid) {
                                lifecycleScope.launch {
                                    dataStoreRepository.saveDataToDataStore(document.id, "document")
                                }

                                when (authority) {
                                    "User" -> {
                                        lifecycleScope.launch {
                                            dataStoreRepository.saveDataToDataStore(
                                                authority,
                                                "Auth"
                                            )
                                        }
                                        navigateToAnotherActivity(MainActivity::class.java)
                                    }

                                    "Editor" -> {
                                        lifecycleScope.launch {
                                            dataStoreRepository.saveDataToDataStore(
                                                authority,
                                                "Auth"
                                            )
                                        }
                                        navigateToAnotherActivity(MainActivity::class.java)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            touchableScreen(R.id.pb_sign)
        }
    }


}