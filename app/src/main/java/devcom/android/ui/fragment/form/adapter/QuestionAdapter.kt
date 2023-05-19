package devcom.android.ui.fragment.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.databinding.ItemQuestionRowBinding
import devcom.android.users.Question
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.showToastMessageFragment

class QuestionAdapter(val questionList : ArrayList<Question>) : RecyclerView.Adapter<QuestionAdapter.AskQuestionHolder>() {

    val db = Firebase.firestore
    var point = 0
    class AskQuestionHolder(val binding: ItemQuestionRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AskQuestionHolder {
        val binding = ItemQuestionRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AskQuestionHolder(binding)
    }

    override fun onBindViewHolder(holder: AskQuestionHolder, position: Int) {
        holder.binding.tvNickname.text = questionList.get(position).QuestionUsername
        holder.binding.tvQuestionHeader.text = questionList.get(position).QuestionHeader
        holder.binding.tvQuestionIntrodoucten.text = questionList.get(position).QuestionContent
        Picasso.get().load(questionList.get(position).QuestionImageProfile).resize(200,200).centerCrop().into(holder.binding.ivProfileQuestion)
        holder.itemView.setOnClickListener{

        }
        holder.binding.ivUp.setOnClickListener {
            point++
            val collectRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)

            val updates = hashMapOf<String,Any>(
                "Point" to point
            )

            collectRef.whereEqualTo(FirebaseConstants.FIELD_QUESTION_HEADER,questionList.get(position).QuestionHeader)
                .get()
                .addOnSuccessListener {documents ->
                    for(document in documents){
                        document.reference.update(updates)
                    }
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Bir şeyler Ters gitti..", Toast.LENGTH_SHORT).show()
                }


        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }


}