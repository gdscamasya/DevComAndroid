package devcom.android.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import devcom.android.R

fun Activity.navigateToAnotherActivity(activity:Class<*>){
    val intent = Intent(this,activity)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent)
}

fun Activity.showToastMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Activity.showSnackBarToMessage(view: View, message: String,){
    Snackbar.make(view,message,Snackbar.LENGTH_LONG).show()
}

fun Activity.touchableScreen(ProgressId:Int){
    val loadingIndicator = findViewById<ProgressBar>(ProgressId)
    loadingIndicator.gone()
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
}

fun Activity.unTouchableScreen(ProgressId:Int){
    val loadingIndicator = findViewById<ProgressBar>(ProgressId)
    loadingIndicator.visible()
    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}