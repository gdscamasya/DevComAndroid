package devcom.android.ui.activity.logins

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import devcom.android.databinding.ActivityPasswordResetBinding

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
            binding.etpEmail.error = "Email adresinizi giriniz."
        }else{
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                AlertDialog.Builder(this).setTitle("Şifrenizi sıfırlamak için e-postanızı kontrol edin.").show()
            }.addOnFailureListener {
                binding.etpEmail.error = "Lütfen kayıtlı bir email adresi giriniz."
            }
        }


    }




}