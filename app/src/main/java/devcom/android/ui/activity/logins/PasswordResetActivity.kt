package devcom.android.ui.activity.logins

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import devcom.android.R
import devcom.android.databinding.ActivityPasswordResetBinding
import devcom.android.utils.extensions.showSnackBarToMessage

class PasswordResetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordResetBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        returnSetOnClickListener()
        passwordRecoverySetOnClickListener()
    }

    private fun returnSetOnClickListener(){
        binding.ivBack.setOnClickListener {
            this.finish()
        }
    }
    private fun passwordRecoverySetOnClickListener(){
        binding.btnRegister.setOnClickListener {
            recoveryPassword()
        }
    }

    private fun recoveryPassword(){
        val email = binding.etpEmail.text.toString()

        if(email.isEmpty()){
            binding.etpEmail.error = getString(R.string.input_email)
        }else{
            auth.fetchSignInMethodsForEmail(email).addOnSuccessListener {
                if(it.signInMethods!!.size > 0 && (it.signInMethods!![0].equals("facebook.com") || it.signInMethods!![0].equals("google.com"))){
                    showSnackBarToMessage(binding.root,getString(R.string.cant_change_password))
                }else{
                    auth.sendPasswordResetEmail(email).addOnSuccessListener {
                        AlertDialog.Builder(this).setTitle(getString(R.string.check_email))
                    }.addOnFailureListener {
                        binding.etpEmail.error = getString(R.string.registered_email)
                    }
                }
            }.addOnFailureListener {
                showSnackBarToMessage(binding.root,getString(R.string.something_went_wrong))
            }

        }


    }




}