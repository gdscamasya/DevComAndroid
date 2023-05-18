package devcom.android.ui.fragment.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import devcom.android.databinding.ItemQuestionRowBinding
import devcom.android.users.Question

class QuestionAdapter(val questionList : ArrayList<Question>) : RecyclerView.Adapter<QuestionAdapter.AskQuestionHolder>() {

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
    }

    override fun getItemCount(): Int {
        return questionList.size
    }
}