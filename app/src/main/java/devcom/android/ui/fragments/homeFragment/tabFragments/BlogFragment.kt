package devcom.android.ui.fragments.homeFragment.tabFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import devcom.android.R


class BlogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_blog, container, false)
        recyclerView = view.findViewById(R.id.rv_blogView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //recyclerView.adapter = BlogRecyclerAdapter()
        return view
        //return inflater.inflate(R.layout.fragment_blog, container, false)
    }

}