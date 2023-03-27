package devcom.android.ui.activity.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import devcom.android.R
import devcom.android.databinding.ActivityEditorBinding

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }


}