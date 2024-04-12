package devcom.android.logic.usecase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import devcom.android.data.repository.DataStoreRepository
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessageFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.URL
import java.util.UUID

var uuidAnswer: String? = null


class AnswerQuestionToSavePersonal(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    private lateinit var dataStoreRepository: DataStoreRepository
    private val uuidIn: UUID = UUID.randomUUID()
    suspend fun answerQuestionToSavePersonal(
        context: Context,
        answerContent: String,
        chooseImageList: ArrayList<Uri>?,
        onSucces: () -> Unit,
        onFailure: () -> Unit
    ) {
        uuidAnswer = uuidIn.toString()

        dataStoreRepository = DataStoreRepository(context)

        val answerList = hashMapOf(
            "AnswerContent" to answerContent
        )

        val document = dataStoreRepository.getDataFromDataStore("document")

        val convertUrlList = convertImageToUrl(chooseImageList)

        val answerImages = hashMapOf(
            "AnswerImgUrlList" to convertUrlList,
            "AnswerContent" to answerContent
        )
        if (document != null) {
            db.collection(FirebaseConstants.COLLECTION_PATH_USERS).document(document)
                .collection("HerAnswers").document(uuidAnswer.toString())
                .set(answerImages).addOnSuccessListener {
                    onSucces()
                }.addOnFailureListener {
                    onFailure()
                }
        } else {
            onFailure()
        }


    }

    private suspend fun convertImageToUrl(chooseImageList: ArrayList<Uri>?): ArrayList<String> {
        val urlList = ArrayList<String>()
        chooseImageList?.forEachIndexed { index, image ->
            val imageRef = storage.reference.child("questionImages").child("$uuidIn+$index.jpg")
            val uploadTask = imageRef.putFile(image)
            Log.i("image = ", "$image\n$uuidIn+$index.jpg")
            try {
                // Yükleme işlemini bekleyerek tamamla
                val getTaskSnapshot = uploadTask.await()
                val downloadUrl = getTaskSnapshot.storage.downloadUrl.await()
                val imageUrl = downloadUrl.toString()
                urlList.add(imageUrl)
            } catch (e: Exception) {
                // Hata işleme kodunu buraya ekleyin
            }
        }
        return urlList
    }

}