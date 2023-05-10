package devcom.android.utils.extensions

private const val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
private const val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$"

 fun String.checkEmail():Boolean{
     return this.matches(emailPattern.toRegex())
 }

fun String.checkPassword():Boolean{
    return this.matches(passwordPattern.toRegex())
}