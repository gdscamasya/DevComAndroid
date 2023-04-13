package devcom.android.ui.fragments.homeFragment

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.R
import devcom.android.ui.activity.main.ProfileActivity
import devcom.android.ui.fragments.homeFragment.adapter.ViewPagerAdapter
import devcom.android.utils.extensions.navigateToAnotherActivity


class HomeFragment : Fragment() {

    val db = Firebase.firestore
    private lateinit var auth : FirebaseAuth
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private val tabTitles = arrayListOf("Blog Yazıları", "Duyurular", "Haberler")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        auth = Firebase.auth
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager = view.findViewById(R.id.view_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val profile = view.findViewById<ImageView>(R.id.iv_profile)

        viewPager.adapter = viewPagerAdapter


        // TabLayout ile ViewPager2'yi bağlayın
        TabLayoutMediator(tabLayout,viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        for (i in 0..3){
            val textview = LayoutInflater.from(view.context).inflate(R.layout.tab_titles,null)
                as TextView
            tabLayout.getTabAt(i)?.customView = textview
        }
    }


}


