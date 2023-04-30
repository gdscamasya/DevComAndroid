package devcom.android.ui.fragment.home.adapter


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import devcom.android.ui.fragment.home.tabFragments.AnnouncementsFragment
import devcom.android.ui.fragment.home.tabFragments.BlogFragment
import devcom.android.ui.fragment.home.tabFragments.NewsFragment


class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BlogFragment()
            1 -> AnnouncementsFragment()
            2 -> NewsFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }




}