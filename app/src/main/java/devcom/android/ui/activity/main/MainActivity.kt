package devcom.android.ui.activity.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.R
import devcom.android.databinding.ActivityMainBinding
import devcom.android.ui.fragments.eventFragment.EventFragment
import devcom.android.ui.fragments.forumFragment.ForumFragment
import devcom.android.ui.fragments.homeFragment.HomeFragment
import devcom.android.ui.fragments.homeFragment.tabFragments.BlogFragment
import devcom.android.ui.fragments.homeFragment.tabFragments.AnnouncementsFragment
import devcom.android.ui.fragments.homeFragment.tabFragments.NewsFragment
import devcom.android.utils.extensions.navigateToAnotherActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var homeFragment:HomeFragment
    private lateinit var forumFragment:ForumFragment
    private lateinit var eventFragment: EventFragment


    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth



        homeFragment = HomeFragment()
        forumFragment = ForumFragment()
        eventFragment = EventFragment()

        replaceFragment(homeFragment)
        navBarSetOnClickListener()



    }

    private fun navBarSetOnClickListener() {
        binding.navActivityMain.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.HomePage ->{
                    replaceFragment(homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.form ->{
                    replaceFragment(forumFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.event ->{
                    replaceFragment(eventFragment)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    private fun replaceFragment(fragment:Fragment){
        val transition = supportFragmentManager.beginTransaction().replace(R.id.fcv_main,fragment).commit()
    }





}