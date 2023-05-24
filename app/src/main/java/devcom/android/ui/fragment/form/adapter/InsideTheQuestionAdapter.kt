package devcom.android.ui.fragment.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import devcom.android.databinding.ItemInsideQuestionRowBinding
import devcom.android.users.InsideQuestion
import devcom.android.users.Question

class InsideTheQuestionAdapter(var insideQuestionList: ArrayList<InsideQuestion?>) : androidx.recyclerview.widget.ListAdapter<Question,InsideTheQuestionAdapter.InsideQuestionHolder>(QuestionInsideDiffCallback()) {


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
        holder.binding.tvNicknameInside.text = insideQuestionList.get(position)?.questionUsername
        holder.binding.tvQuestionInsideIntrodoucten.text = insideQuestionList.get(position)?.questionContent
        Picasso.get().load(insideQuestionList.get(position)?.questionImageUri).resize(200,200).centerCrop().into(holder.binding.ivProfileInsideQuestion)
    }


}