package devcom.android.ui.fragment.form.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import devcom.android.ui.fragment.form.QuestionsFragment
import devcom.android.ui.fragment.form.TopVotedFragment
import devcom.android.ui.fragment.form.UnAnsweredFragment

class FormViewPagerAdapter(fragment:Fragment):FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        Log.i("PageChange","TopVoted Sayfasına gidicek..")

        return when (position) {
            0 -> TopVotedFragment()
            1 -> QuestionsFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

}