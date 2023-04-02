package devcom.android.users

data class User(

    val username: String? = null,
    val uuid:String? = null,
    val authority:String? = null

)

data class uuid(
    val uuid:String? = null
)

data class usernameAndAuth(
    val username: String? = null,
    val authority: String? = null
)