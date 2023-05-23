package devcom.android.ui.fragment.form

import android.content.Context
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.R
import devcom.android.ui.activity.main.MainActivity
import devcom.android.ui.fragment.form.adapter.FormViewPagerAdapter
import devcom.android.ui.fragment.home.HomeFragmentDirections
import devcom.android.ui.fragment.profile.ProfileFragmentDirections
import devcom.android.utils.constants.FirebaseConstants


class FormFragment : Fragment() {

    private val tabTitles = arrayListOf("Popüler Sorular", "Sorular")

    val db = Firebase.firestore
    private lateinit var auth : FirebaseAuth


    private lateinit var searchBar:SearchView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var tabLayout:TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerFormAdapter: FormViewPagerAdapter
    private lateinit var profileImageView:ImageView
    private lateinit var addQuestionMenu: ImageView
    val fragments = listOf(TopVotedFragment(),QuestionsFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerFormAdapter = FormViewPagerAdapter(this)
        viewPager2 = view.findViewById(R.id.view_pager_form)
        tabLayout = view.findViewById(R.id.tab_layout_form)
        profileImageView = view.findViewById(R.id.iv_profile_forum)
        addQuestionMenu = view.findViewById(R.id.vector_menu)
        searchBar = view.findViewById(R.id.searcher)
        bottomNav.visibility = View.VISIBLE
        auth = Firebase.auth

        profileImageView.setOnClickListener {
            val action = FormFragmentDirections.actionFormToProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

        viewPager2.adapter = viewPagerFormAdapter

        TabLayoutMediator(tabLayout,viewPager2) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        for (i in 0..1){
            val textview = LayoutInflater.from(view.context).inflate(R.layout.tab_titles,null) as TextView
            tabLayout.getTabAt(i)?.customView = textview
        }

        getData()
        getDataQuestions()
        addQuestionSetOnClickListener()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            bottomNav = context.getBottomView()
        }
    }

    private fun addQuestionSetOnClickListener(){
        addQuestionMenu.setOnClickListener{
            popUpMenu()
        }
    }
    private fun popUpMenu(){
        val popupMenu = PopupMenu(requireContext(),addQuestionMenu)
        popupMenu.inflate(R.menu.form_menu_items)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.add_question ->{
                    val action = FormFragmentDirections.actionFormToAskQuestionFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                    bottomNav.visibility = View.INVISIBLE
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }



    private fun getData(){

        db.collection(FirebaseConstants.COLLECTION_PATH_USERS).addSnapshotListener{ value, error ->
            if(error != null){
                Toast.makeText(requireContext(), "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(document in documents){

                            val uuid = document.get(FirebaseConstants.FIELD_UUID) as? String
                            val downloadUrl = document.get(FirebaseConstants.FIELD_DOWNLOAD_URL) as? String


                            if(uuid == auth.currentUser!!.uid){

                                if(downloadUrl != null){
                                    Picasso.get().load(downloadUrl).resize(200,200).centerCrop().into(profileImageView)
                                }else{
                                    continue
                                }
                            }
                        }
                        viewPagerFormAdapter.notifyDataSetChanged()
                    }
                }
            }

        }
    }

    private fun getDataQuestions(){
        val refCollect = db.collection(FirebaseConstants.COLLECTION_PATH_QUESTIONS)

        refCollect.addSnapshotListener{ value,error ->
            if(error != null){
                Toast.makeText(requireContext(), "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        viewPagerFormAdapter.notifyDataSetChanged()
                    }
                }

            }


        }


    }




}


