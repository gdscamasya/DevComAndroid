package devcom.android.logic.usecase

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class GetData(private val auth: FirebaseAuth, private val db: FirebaseFirestore, private val storage: FirebaseStorage) {

      fun getData(onSucces : (String) -> Unit, onFailure : () -> Unit){
        db.collection("users").addSnapshotListener{ value,error ->
            if(error != null){
                onFailure()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(document in documents){

                            val uuid = document.get("uuid") as? String
                            val username = document.get("username") as? String
                            val downloadUrl = document.get("downloadUrl") as? String

                            if(uuid == auth.currentUser!!.uid){

                                onSucces(username ?: "")

                            }
                        }
                    }
                }
            }

        }
    }

}