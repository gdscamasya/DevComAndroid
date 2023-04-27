package devcom.android.ui.fragment.form.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import devcom.android.ui.fragment.form.QuestionsFragment
import devcom.android.ui.fragment.form.TopVotedFragment
import devcom.android.ui.fragment.form.UnAnsweredFragment

class FormViewPagerAdapter(fragment:Fragment):FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TopVotedFragment()
            1 -> QuestionsFragment()
            2 -> UnAnsweredFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

}