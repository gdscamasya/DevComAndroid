package devcom.android.ui.fragment.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import devcom.android.databinding.ItemInsideQuestionRowBinding
import devcom.android.users.Answer
import devcom.android.users.InsideQuestion
import devcom.android.users.Question

class InsideTheQuestionAdapter(private var insideQuestionList: ArrayList<Any?>) : androidx.recyclerview.widget.ListAdapter<Question,InsideTheQuestionAdapter.InsideQuestionHolder>(QuestionInsideDiffCallback()) {


    class QuestionInsideDiffCallback : DiffUtil.ItemCallback<Question>() {
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

    class InsideQuestionHolder(val binding: ItemInsideQuestionRowBinding):RecyclerView.ViewHolder(binding.root){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsideQuestionHolder {
        val binding = ItemInsideQuestionRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return InsideQuestionHolder(binding)
    }

    override fun getItemCount(): Int {
        return insideQuestionList.size
    }

    override fun onBindViewHolder(holder: InsideQuestionHolder, position: Int) {
        val item = insideQuestionList[position]
        if(item is InsideQuestion){
            val insideQuestion = item as InsideQuestion
            holder.binding.tvNicknameInside.text = insideQuestion.questionUsername
            holder.binding.tvQuestionInsideIntrodoucten.text = insideQuestion.questionContent
            if(insideQuestion.questionImageUri != null){
                Picasso.get().load(insideQuestion.questionImageUri).resize(200,200).centerCrop().into(holder.binding.ivProfileInsideQuestion)
            }
        }else if(item is Answer){
            val answer = item as Answer
            holder.binding.tvNicknameInside.text = answer.answerUsername
            holder.binding.tvQuestionInsideIntrodoucten.text = answer.answerContent
            if(answer.answerProfileImage != null){
                Picasso.get().load(answer.answerProfileImage).resize(200,200).centerCrop().into(holder.binding.ivProfileInsideQuestion)
            }
        }

    }


}