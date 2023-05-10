package devcom.android.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.R
import devcom.android.ui.fragment.home.adapter.ViewPagerAdapter
import devcom.android.utils.constants.FirebaseConstants.COLLECTION_PATH_USERS
import devcom.android.utils.constants.FirebaseConstants.FIELD_DOWNLOAD_URL
import devcom.android.utils.constants.FirebaseConstants.FIELD_UUID


class HomeFragment : Fragment() {

    val db = Firebase.firestore
    private lateinit var auth : FirebaseAuth
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var profileImageView:ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout:TabLayout


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
        tabLayout = view.findViewById(R.id.tab_layout)
        profileImageView = view.findViewById(R.id.iv_profile_question)


        profileImageView.setOnClickListener {
            val action = HomeFragmentDirections.actionHomePageToProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

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

        getData()

    }


    private fun getData(){

        db.collection(COLLECTION_PATH_USERS).addSnapshotListener{ value,error ->
            if(error != null){
                Toast.makeText(requireContext(), "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(document in documents){

                            val uuid = document.get(FIELD_UUID) as? String
                            val downloadUrl = document.get(FIELD_DOWNLOAD_URL) as? String


                            if(uuid == auth.currentUser!!.uid){

                                if(downloadUrl != null){
                                    Picasso.get().load(downloadUrl).resize(200,200).centerCrop().into(profileImageView)
                                }else{
                                    continue
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}


