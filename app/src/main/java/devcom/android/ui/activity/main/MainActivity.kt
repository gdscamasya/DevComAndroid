package devcom.android.ui.activity.main

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import devcom.android.R
import devcom.android.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNav:BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        bottomNav = findViewById(R.id.nav_activityMain)
        val navController = findNavController(R.id.fcv_main_host)
        bottomNav.setupWithNavController(navController)

        //val navGraph = findViewById<FragmentContainerView>(R.id.fcv_main_host)
        //val mainNavGraph = NavGraph(NavGraphNavigator(userNavGraph))
    }


    fun getBottomView(): BottomNavigationView {
        return bottomNav
    }


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


