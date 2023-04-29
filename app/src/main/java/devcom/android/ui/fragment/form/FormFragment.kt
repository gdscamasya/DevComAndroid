package devcom.android.ui.fragment.form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import devcom.android.R
import devcom.android.ui.fragment.form.adapter.FormViewPagerAdapter


class FormFragment : Fragment() {

    private val tabTitles = arrayListOf("Popüler Sorular", "Sorular", "Cevapsız Sorular")

    private lateinit var tabLayout:TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerFormAdapter: FormViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerFormAdapter = FormViewPagerAdapter(this)
        viewPager2 = view.findViewById(R.id.view_pager_form)
        tabLayout = view.findViewById(R.id.tab_layout_form)

        viewPager2.adapter = viewPagerFormAdapter

        TabLayoutMediator(tabLayout,viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
        for (i in 0..3){
            val textview = LayoutInflater.from(view.context).inflate(R.layout.tab_titles,null)
                    as TextView
            tabLayout.getTabAt(i)?.customView = textview
        }

    }

}


