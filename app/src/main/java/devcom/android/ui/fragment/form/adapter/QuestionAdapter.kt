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
import devcom.android.data.repository.DataStoreRepository
import devcom.android.databinding.ItemQuestionRowBinding
import devcom.android.logic.usecase.uuid
import devcom.android.ui.fragment.form.likedIndexQuestions

import devcom.android.users.Question
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.utils.extensions.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



private lateinit var formViewPagerAdapater:FormViewPagerAdapter
class QuestionAdapter(var questionList : ArrayList<Question>) : ListAdapter<Question, QuestionAdapter.AskQuestionHolder>(QuestionDiffCallback()) {

    lateinit var dataStoreRepository: DataStoreRepository
    val db = Firebase.firestore
    var point : Long = 0



    class QuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.QuestionContent == newItem.QuestionContent
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }

    fun submitData(newQuestionList: List<Question>) {
        submitList(newQuestionList)
    }

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
        holder.binding.tvUp.text = questionList.get(position).QuestionPoint

        for(liking in likedIndexQuestions){
            if(position == liking){
                holder.binding.ivUp.visibility = View.INVISIBLE
                holder.binding.ivDown.visibility = View.VISIBLE
            }
        }


        holder.itemView.setOnClickListener{

        }

        holder.binding.ivUp.setOnClickListener {

            val collectRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)

            CoroutineScope(Dispatchers.Main).launch{
                dataStoreRepository = DataStoreRepository(holder.itemView.context)
                val document = dataStoreRepository.getDataFromDataStore("document")

                if (document != null) {
                    db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                        .document(document)
                        .collection("LikedQuestions").document(questionList[position].docNum!!)
                        .set(emptyMap<String,Any>())
                        .addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "likedQuestion", Toast.LENGTH_SHORT).show()
                            updateViewPager()
                        }
                        .addOnFailureListener {
                            // Hata durumunda yapılacak işlemler
                        }
                }
            }

            collectRef.whereEqualTo(FirebaseConstants.FIELD_QUESTION_HEADER,questionList.get(position).QuestionHeader)
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
                        questionList[position].QuestionPoint = point.toString()

                    }
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Bir şeyler Ters gitti..", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun updateViewPager(){
        formViewPagerAdapater.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return questionList.size
    }


}