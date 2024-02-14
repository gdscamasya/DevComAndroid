package devcom.android.utils.extensions

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.navigateToAnotherActivity(activity:Class<*>){
    val intent = Intent(getActivity(),activity)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
}

fun Fragment.showToastMessageFragment(message: String){
    Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
}
fun Fragment.touchableScreen(view:View, ProgressId:Int){
    val loadingIndicator = view.findViewById<ProgressBar>(ProgressId)
    loadingIndicator.gone()
    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
}
fun Fragment.unTouchableScreen(view:View, ProgressId:Int){
    val loadingIndicator = view.findViewById<ProgressBar>(ProgressId)
    loadingIndicator.visible()
    requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

