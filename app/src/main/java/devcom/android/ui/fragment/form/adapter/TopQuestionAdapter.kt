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
import devcom.android.ui.fragment.form.likedIndexQuestionsTopVoted
import devcom.android.data.Question
import devcom.android.ui.fragment.form.RecyclerViewItemClickListener
import devcom.android.ui.fragment.form.TopRecyclerViewItemClickListener
import devcom.android.utils.constants.FirebaseConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TopQuestionAdapter(var topQuestionList : ArrayList<Question>, private val itemViewListener : TopRecyclerViewItemClickListener, private val likeClickListener : TopRecyclerViewItemClickListener) : ListAdapter<Question,TopQuestionAdapter.TopQuestionHolder>(TopQuestionDiffCallback()){

    lateinit var dataStoreRepository: DataStoreRepository
    val db = Firebase.firestore
    var point : Long = 0

    private fun setMaxCharacterLimit(textView: TextView, maxLimit: Int) {
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
            return oldItem.questionPoint == newItem.questionPoint
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
        holder.binding.tvNickname.text = topQuestionList.get(position).questionUsername
        setMaxCharacterLimit(holder.binding.tvQuestionHeader,100)
        holder.binding.tvQuestionHeader.text = topQuestionList.get(position).questionHeader
        setMaxCharacterLimit(holder.binding.tvQuestionIntrodoucten,100)
        holder.binding.tvQuestionIntrodoucten.text = topQuestionList.get(position).questionContent

        if(topQuestionList.get(position).questionImageProfile != null){
            Picasso.get().load(topQuestionList.get(position).questionImageProfile).resize(200,200).centerCrop().into(holder.binding.ivProfileQuestion)
        }

        holder.binding.tvUp.text = topQuestionList.get(position).questionPoint

        holder.binding.ivLiking.visibility = if (topQuestionList.get(position).likingViewVisible) View.INVISIBLE else View.VISIBLE
        holder.binding.ivLiked.visibility = if (topQuestionList.get(position).likingViewVisible) View.VISIBLE else View.INVISIBLE


        holder.itemView.setOnClickListener{

            itemViewListener.onClick(topQuestionList.get(position).docNum)

        }


        holder.binding.ivLiking.setOnClickListener {

            likeClickListener.onClick(position)

        }
    }

    override fun getItemCount(): Int {
        return topQuestionList.size
    }

}