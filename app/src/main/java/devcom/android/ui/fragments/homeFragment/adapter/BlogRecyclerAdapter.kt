package devcom.android.ui.fragments.homeFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import devcom.android.databinding.MainBlogPagerowBinding
import devcom.android.users.User

class BlogRecyclerAdapter(private val userList: List<User>):RecyclerView.Adapter<BlogRecyclerAdapter.BlogHolder>() {

    class BlogHolder(binding:MainBlogPagerowBinding) : RecyclerView.ViewHolder(binding.root){



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogHolder {
        val binding = MainBlogPagerowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BlogHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}