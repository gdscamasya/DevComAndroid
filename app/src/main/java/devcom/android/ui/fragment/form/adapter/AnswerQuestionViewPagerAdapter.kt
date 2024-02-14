package devcom.android.ui.fragment.form.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener
import com.squareup.picasso.Picasso
import devcom.android.R
import devcom.android.ui.fragment.form.AnswerQuestionFragment


    class AnswerQuestionViewPagerAdapter(
        private val context: Context,
        private val imagesUrls: ArrayList<Uri>
    ) :
        PagerAdapter() {

        override fun getCount(): Int {
            return (imagesUrls.size / 2) + (imagesUrls.size % 2)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val view = LayoutInflater.from(context).inflate(R.layout.question_images_item, container, false)
            val imageView: ImageView = view.findViewById(R.id.iv_UploadImages)
            val imageViewTwo: ImageView = view.findViewById(R.id.iv_UploadImages2)

            val selectedImages1 = if(imagesUrls.size > position * 2) imagesUrls[position * 2] else null
            val selectedImages2 = if (imagesUrls.size > position * 2 + 1) imagesUrls[position * 2 + 1] else null

            imageView.setImageURI(selectedImages1)
            imageViewTwo.setImageURI(selectedImages2)

            container.addView(view)
            return view
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

    }