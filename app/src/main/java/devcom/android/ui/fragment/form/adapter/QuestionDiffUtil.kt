package devcom.android.ui.fragment.form.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import devcom.android.data.Question

class QuestionDiffUtil(
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
        return when {
            oldList[oldItemPosition].questionPoint != newList[newItemPosition].questionPoint -> {
                false
            }

            oldList[oldItemPosition].likingViewVisible != newList[newItemPosition].likingViewVisible -> {
                Log.i("questionPoints", oldList[oldItemPosition].likingViewVisible.toString())
                Log.i("questionPoints", newList[oldItemPosition].likingViewVisible.toString())
                false
            }

            oldList[oldItemPosition].questionUsername != newList[newItemPosition].questionUsername -> {
                false
            }

            oldList[oldItemPosition].questionImageProfile != newList[newItemPosition].questionImageProfile -> {
                false
            }

            else -> true
        }
    }
}
