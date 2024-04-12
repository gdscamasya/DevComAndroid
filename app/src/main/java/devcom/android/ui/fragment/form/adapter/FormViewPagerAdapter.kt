package devcom.android.ui.fragment.form.adapter

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import devcom.android.ui.fragment.form.QuestionsFragment
import devcom.android.ui.fragment.form.TopVotedFragment

class FormViewPagerAdapter(fragment: androidx.fragment.app.FragmentManager, lifecycle: Lifecycle):FragmentStateAdapter(fragment,lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TopVotedFragment()
            1 -> QuestionsFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

}