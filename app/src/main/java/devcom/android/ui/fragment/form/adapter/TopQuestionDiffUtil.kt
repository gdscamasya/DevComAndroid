package devcom.android.ui.fragment.form.adapter

import androidx.recyclerview.widget.DiffUtil
import devcom.android.data.Question

class TopQuestionDiffUtil(
    private val oldList: ArrayList<Question>,
    private val newList: ArrayList<Question>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition].questionPoint != newList[newItemPosition].questionPoint ->{
                false
            }oldList[oldItemPosition].likingViewVisible != newList[newItemPosition].likingViewVisible ->{
                false
            }oldList[oldItemPosition].questionUsername != newList[newItemPosition].questionUsername ->{
                false
            }oldList[oldItemPosition].questionImageProfile != newList[newItemPosition].questionImageProfile ->{
                false
            }
            else -> true
        }
    }
}