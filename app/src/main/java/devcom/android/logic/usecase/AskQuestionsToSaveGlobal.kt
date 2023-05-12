package devcom.android.logic.usecase

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import devcom.android.utils.Resource
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessageFragment
import java.lang.Exception
import java.net.URI
import java.util.*

class AskQuestionsToSaveGlobal(private val auth: FirebaseAuth, private val db: FirebaseFirestore, private val storage: FirebaseStorage) {

    fun askQuestionToSaveGlobal( questionContent: String, questionHeader: String,questionTags: String ,selectedPicture:Uri?, onSucces : () -> Unit, onFailure : () -> Unit) {

        val uuids = UUID.randomUUID()
        val imageName = "$uuids.jpg"

        val reference = storage.reference
        val imageRef = reference.child("questionImages").child(imageName)

        if (selectedPicture == null) {
            val questions = hashMapOf<String, Any>(
                "QuestionContent" to questionContent,
                "QuestionHeader" to questionHeader,
                "QuestionTags" to questionTags
            )

            db.collection(FirebaseConstants.COLLECTİON_PATH_QUESTİONS)
                .add(questions)
                .addOnSuccessListener {
                    onSucces()
                }.addOnFailureListener {
                    onFailure()
                }
        } else {
            imageRef.putFile(selectedPicture).addOnSuccessListener {
                //download url -> firestore a aktaracaz
                val uploadPicRef = storage.reference.child("questionImages").child(imageName)
                uploadPicRef.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()

                    val questions = hashMapOf<String, Any>(
                        "QuestionContent" to questionContent,
                        "QuestionHeader" to questionHeader,
                        "QuestionTags" to questionTags,
                        "QuestionImage" to downloadUrl
                    )

                    db.collection(FirebaseConstants.COLLECTİON_PATH_QUESTİONS)
                        .add(questions)
                        .addOnSuccessListener {
                            onSucces()
                        }.addOnFailureListener {
                            onFailure()
                        }

                }
            }.addOnFailureListener{
                onFailure()
            }
        }
    }


}