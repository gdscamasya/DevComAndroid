package devcom.android.ui.fragment.form.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import devcom.android.ui.fragment.form.QuestionsFragment
import devcom.android.ui.fragment.form.TopVotedFragment
import devcom.android.ui.fragment.form.UnAnsweredFragment

class FormViewPagerAdapter(fragment:Fragment):FragmentStateAdapter(fragment) {

    private val fragmentList = mutableListOf<Fragment>()


    fun setFragments(fragments: List<Fragment>) {
        fragmentList.clear()
        fragmentList.addAll(fragments)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }



}