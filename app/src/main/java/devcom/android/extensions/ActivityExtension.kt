package devcom.android.extensions

import android.app.Activity
import android.content.Intent

fun Activity.navigateToAnotherActivity(activity:Class<*>){
    val intent = Intent(this,activity)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent)
}