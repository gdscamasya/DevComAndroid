package devcom.android.utils.extensions

private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

 fun String.checkEmail():Boolean{
     return this.matches(emailPattern.toRegex())
 }