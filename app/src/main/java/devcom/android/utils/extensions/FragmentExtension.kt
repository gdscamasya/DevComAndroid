package devcom.android.utils.extensions

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.navigateToAnotherActivity(activity:Class<*>){
    val intent = Intent(getActivity(),activity)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent)
}

