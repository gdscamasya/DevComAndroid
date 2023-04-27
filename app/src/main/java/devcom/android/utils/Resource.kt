package devcom.android.utils

sealed class Resource<T>(
    val data: T? = null,
    val error:String
){
    class Success<T>(data: T? = null) : Resource<T>(data, "")
    class Error<T>(error: String) : Resource<T>(null,error)
}

