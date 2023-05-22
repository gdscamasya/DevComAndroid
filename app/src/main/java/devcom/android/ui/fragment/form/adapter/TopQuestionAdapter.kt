package devcom.android.ui.fragment.form.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.databinding.ItemQuestionRowBinding
import devcom.android.users.Question
import devcom.android.utils.constants.FirebaseConstants

class TopQuestionAdapter(var topQuestionList : ArrayList<Question>) : ListAdapter<Question,TopQuestionAdapter.TopQuestionHolder>(TopQuestionDiffCallback()){

    val db = Firebase.firestore
    var point : Long = 0

    class TopQuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.QuestionPoint == newItem.QuestionPoint
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }

    fun submitDataTopVoted(newQuestionList: List<Question>) {
        submitList(newQuestionList)
    }


    class TopQuestionHolder(val binding:ItemQuestionRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopQuestionHolder {
        val binding = ItemQuestionRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TopQuestionHolder(binding)
    }

    override fun onBindViewHolder(holder: TopQuestionHolder, position: Int) {
        holder.binding.tvNickname.text = topQuestionList.get(position).QuestionUsername
        holder.binding.tvQuestionHeader.text = topQuestionList.get(position).QuestionHeader
        holder.binding.tvQuestionIntrodoucten.text = topQuestionList.get(position).QuestionContent
        Picasso.get().load(topQuestionList.get(position).QuestionImageProfile).resize(200,200).centerCrop().into(holder.binding.ivProfileQuestion)
        holder.binding.tvUp.text = topQuestionList.get(position).QuestionPoint

        holder.itemView.setOnClickListener{

        }
        holder.binding.ivUp.setOnClickListener {
            val collectRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)

            collectRef.whereEqualTo(FirebaseConstants.FIELD_QUESTION_HEADER,topQuestionList.get(position).QuestionHeader)
                .get()
                .addOnSuccessListener {documents ->
                    for(document in documents){
                        point = document.getLong("Point")!!

                        point++

                        val updates = hashMapOf<String,Any>(
                            "Point" to point
                        )

                        document.reference.update(updates)

                        holder.binding.tvUp.text = point.toString()
                        holder.binding.ivUp.visibility = View.INVISIBLE
                        holder.binding.ivDown.visibility = View.VISIBLE
                        topQuestionList[position].QuestionPoint = point.toString()

                    }
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Bir şeyler Ters gitti..", Toast.LENGTH_SHORT).show()
                }

        }
    }

    override fun getItemCount(): Int {
        return topQuestionList.size
    }

}