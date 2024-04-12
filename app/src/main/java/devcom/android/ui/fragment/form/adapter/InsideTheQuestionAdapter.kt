package devcom.android.ui.fragment.form.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import devcom.android.databinding.ItemInsideQuestionRowBinding
import devcom.android.data.Answer
import devcom.android.data.InsideQuestion
import devcom.android.data.Question

class InsideTheQuestionAdapter(private var insideQuestionList: ArrayList<Any?>) : androidx.recyclerview.widget.ListAdapter<Any?,InsideTheQuestionAdapter.InsideQuestionHolder>(QuestionInsideDiffCallback()) {


    class QuestionInsideDiffCallback : DiffUtil.ItemCallback<Any?>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if(oldItem is InsideQuestion && newItem is InsideQuestion){
                oldItem.questionContent == newItem.questionContent
            }else if (oldItem is Answer && newItem is Answer){
                oldItem.answerContent == newItem.answerContent
            }else{
                oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if(oldItem is InsideQuestion && newItem is InsideQuestion){
                oldItem == newItem
            }else if (oldItem is Answer && newItem is Answer){
                oldItem == newItem
            }else{
                oldItem == newItem
            }
        }
    }

    fun submitData(newQuestionList: List<Any?>) {
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
            //User -> Ask Question
            val insideQuestion = item as InsideQuestion
            holder.binding.tvNicknameInside.text = insideQuestion.questionUsername
            holder.binding.tvQuestionInsideIntrodoucten.text = insideQuestion.questionContent
            if(insideQuestion.questionImageUri != null){
                Picasso.get().load(insideQuestion.questionImageUri).resize(200,200).centerCrop().into(holder.binding.ivProfileInsideQuestion)
            }
        }else if(item is Answer){
            //User -> Answer
            val answer = item as Answer
            holder.binding.tvNicknameInside.text = answer.answerUsername
            holder.binding.tvQuestionInsideIntrodoucten.text = answer.answerContent
            if(answer.answerProfileImage != null){
                Picasso.get().load(answer.answerProfileImage).resize(200,200).centerCrop().into(holder.binding.ivProfileInsideQuestion)
            }
            val imageUrlsList = answer.answerAddingImage
            if(imageUrlsList != null){
                holder.binding.viewPagerImagesLink.adapter = InsideTheQuestionAdapterToAdapter(holder.itemView.context, imagesUrls = imageUrlsList)
            }

        }

    }


}