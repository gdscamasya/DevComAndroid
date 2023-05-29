package devcom.android.ui.fragment.form.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.data.repository.DataStoreRepository
import devcom.android.databinding.ItemQuestionRowBinding
import devcom.android.ui.fragment.form.RecyclerViewItemClickListener

import devcom.android.data.Question


class QuestionRecyclerAdapter(var questionList : ArrayList<Question>, val itemViewListener : RecyclerViewItemClickListener, val likeClickListener : RecyclerViewItemClickListener) : ListAdapter<Question, QuestionRecyclerAdapter.AskQuestionHolder>(QuestionDiffCallback()) {

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

    class QuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.questionContent == newItem.questionContent
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
        holder.binding.tvNickname.text = questionList.get(position).questionUsername

        setMaxCharacterLimit(holder.binding.tvQuestionHeader,100)
        holder.binding.tvQuestionHeader.text = questionList.get(position).questionHeader

        setMaxCharacterLimit(holder.binding.tvQuestionIntrodoucten,100)
        holder.binding.tvQuestionIntrodoucten.text = questionList.get(position).questionContent

        if(questionList.get(position).questionImageProfile != null){
            Picasso.get().load(questionList.get(position).questionImageProfile).resize(200,200).centerCrop().into(holder.binding.ivProfileQuestion)
        }
        holder.binding.tvUp.text = questionList.get(position).questionPoint

        holder.binding.ivLiking.visibility = if (questionList.get(position).likingViewVisible) View.INVISIBLE else View.VISIBLE
        holder.binding.ivLiked.visibility = if (questionList.get(position).likingViewVisible) View.VISIBLE else View.INVISIBLE


        holder.itemView.setOnClickListener{

            itemViewListener.onClick(questionList.get(position).docNum)

        }

        holder.binding.ivLiking.setOnClickListener {

            likeClickListener.onClick(position)

        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

}