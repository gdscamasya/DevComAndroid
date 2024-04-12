package devcom.android.ui.fragment.form.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso
import devcom.android.R
import devcom.android.ui.fragment.form.size.FullScreenImageFragment

class InsideTheQuestionAdapterToAdapter(private val context: Context,
                                        private val imagesUrls: ArrayList<*>):PagerAdapter() {
    override fun getCount(): Int {
        return (imagesUrls.size / 2) + (imagesUrls.size % 2)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "$position"
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(context).inflate(R.layout.question_images_item, container, false)
        val imageView: ImageView = view.findViewById(R.id.iv_UploadImages)
        val imageViewTwo: ImageView = view.findViewById(R.id.iv_UploadImages2)

        val selectedImages1 = if(imagesUrls.size > position * 2) imagesUrls[position * 2] else null
        val selectedImages2 = if (imagesUrls.size > position * 2 + 1) imagesUrls[position * 2 + 1] else null

        Picasso.get().load(selectedImages1.toString()).resize(200,200).centerCrop().into(imageView)
        Picasso.get().load(selectedImages2.toString()).resize(200,200).centerCrop().into(imageViewTwo)


        imageView.setOnClickListener {
            val fullScreenImageFragment = FullScreenImageFragment.newInstance(selectedImages1.toString())
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fullScreenImageFragment.show(fragmentManager, "FullScreenImageFragment")
        }

        imageViewTwo.setOnClickListener {
            val fullScreenImageFragment = FullScreenImageFragment.newInstance(selectedImages2.toString())
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fullScreenImageFragment.show(fragmentManager, "FullScreenImageFragment")
        }

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