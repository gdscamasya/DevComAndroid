package devcom.android.ui.fragment.form.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.data.repository.DataStoreRepository
import devcom.android.databinding.ItemQuestionRowBinding
import devcom.android.ui.fragment.form.FormFragmentDirections
import devcom.android.ui.fragment.form.likedIndexQuestions
import devcom.android.ui.fragment.form.likedIndexQuestionsTopVoted
import devcom.android.users.Question
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TopQuestionAdapter(var topQuestionList : ArrayList<Question>) : ListAdapter<Question,TopQuestionAdapter.TopQuestionHolder>(TopQuestionDiffCallback()){

    lateinit var dataStoreRepository: DataStoreRepository
    val db = Firebase.firestore
    var point : Long = 0

    fun setMaxCharacterLimit(textView: TextView, maxLimit: Int) {
        textView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Değişiklik öncesi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Değişiklik sırasında
                if (s?.length ?: 0 > maxLimit) {
                    val trimmedText = s?.substring(0, maxLimit)
                    textView.text = trimmedText
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Değişiklik sonrası
            }
        })
    }

    class TopQuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.QuestionContent == newItem.QuestionContent
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
        setMaxCharacterLimit(holder.binding.tvQuestionHeader,100)
        holder.binding.tvQuestionHeader.text = topQuestionList.get(position).QuestionHeader
        setMaxCharacterLimit(holder.binding.tvQuestionIntrodoucten,100)
        holder.binding.tvQuestionIntrodoucten.text = topQuestionList.get(position).QuestionContent

        if(topQuestionList.get(position).QuestionImageProfile != null){
            Picasso.get().load(topQuestionList.get(position).QuestionImageProfile).resize(200,200).centerCrop().into(holder.binding.ivProfileQuestion)
        }
        holder.binding.tvUp.text = topQuestionList.get(position).QuestionPoint

        for(liking in likedIndexQuestionsTopVoted){
            if(position == liking){
                holder.binding.ivUp.visibility = View.INVISIBLE
                holder.binding.ivDown.visibility = View.VISIBLE
            }
        }

        holder.itemView.setOnClickListener{
            val action = FormFragmentDirections.actionFormToInsideTheQuestionFragment(topQuestionList.get(position).docNum)
            Navigation.findNavController(holder.itemView).navigate(action)
        }


        holder.binding.ivUp.setOnClickListener {
            val collectRef = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)

            CoroutineScope(Dispatchers.Main).launch{
                dataStoreRepository = DataStoreRepository(holder.itemView.context)
                val document = dataStoreRepository.getDataFromDataStore("document")

                if (document != null) {
                    db.collection(FirebaseConstants.COLLECTION_PATH_USERS)
                        .document(document)
                        .collection("LikedQuestions").document(topQuestionList[position].docNum!!)
                        .set(emptyMap<String,Any>())
                        .addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "likedQuestion", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            // Hata durumunda yapılacak işlemler
                        }
                }
            }

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