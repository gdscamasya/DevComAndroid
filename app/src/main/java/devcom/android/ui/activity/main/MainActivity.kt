package devcom.android.ui.activity.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import devcom.android.R
import devcom.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.navActivityMain.selectedItemId = R.id.HomePage
    }

    fun blogClick(view: View){
        binding.btnBlog.background= getDrawable(R.drawable.main_button_v2)
        binding.btnBlog.setTextColor(getColor(R.color.backgroundWhite))
    }
}