package devcom.android.ui.activity.main

import android.os.Bundle
<<<<<<< HEAD
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
=======
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
>>>>>>> FormDesign
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
<<<<<<< HEAD
    private lateinit var auth: FirebaseAuth
    private lateinit var homeFragment:HomeFragment
    private lateinit var forumFragment:ForumFragment
    private lateinit var eventFragment: EventFragment


    val db = Firebase.firestore
=======
    private lateinit var bottomNav:BottomNavigationView
>>>>>>> FormDesign

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

<<<<<<< HEAD
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




=======

        bottomNav = findViewById(R.id.nav_activityMain)
        val navController = findNavController(R.id.fcv_main_host)
        bottomNav.setupWithNavController(navController)

        //val navGraph = findViewById<FragmentContainerView>(R.id.fcv_main_host)
        //val mainNavGraph = NavGraph(NavGraphNavigator(userNavGraph))

    }

    fun getBottomView(): BottomNavigationView {
        return bottomNav
    }


    /*override fun onBackPressed() {
        navController.previousBackStackEntry?.let {
            val destinationFragment = it.destination.id
            if (childFragmentList.contains(destinationFragment)){
                navView.menu[childFragmentMenuList[childFragmentList.indexOf(destinationFragment)]].isChecked = true
            }
        }

        super.onBackPressed()
    }

     */
>>>>>>> FormDesign

}