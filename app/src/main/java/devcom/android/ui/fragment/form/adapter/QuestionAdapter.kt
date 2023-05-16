package devcom.android.ui.fragment.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import devcom.android.databinding.ItemQuestionRowBinding
import devcom.android.users.User

class QuestionAdapter(val QuestionList : ArrayList<User>) : RecyclerView.Adapter<QuestionAdapter.AskQuestionHolder>() {

    class AskQuestionHolder(val binding: ItemQuestionRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AskQuestionHolder {
        val binding = ItemQuestionRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AskQuestionHolder(binding)
    }

    override fun onBindViewHolder(holder: AskQuestionHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return QuestionList.size
    }
}