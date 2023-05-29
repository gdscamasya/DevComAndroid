package devcom.android.data

data class Question(
    val docNum: String?,
    val questionImageProfile: String?,
    val questionUsername: String?,
    val questionContent: String?,
    val questionHeader: String?,
    val questionImageUri: String?,
    val questionTags: String?,
    var questionPoint: String?,
    var likingViewVisible: Boolean
) {

}
