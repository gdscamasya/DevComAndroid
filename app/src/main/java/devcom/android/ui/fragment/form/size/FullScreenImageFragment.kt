package devcom.android.ui.fragment.form.size

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.squareup.picasso.Picasso
import devcom.android.R

class FullScreenImageFragment : DialogFragment() {

    private var imageUrl: String? = null

    companion object {
        private const val ARG_IMAGE_URL = "imageUrl"

        fun newInstance(imageUrl: String): FullScreenImageFragment {
            val fragment = FullScreenImageFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(ARG_IMAGE_URL)
        }
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full_screen_image, container, false)
        val imageView: ImageView = view.findViewById(R.id.imageViewFullScreen)

        imageUrl?.let {
            Picasso.get().load(it).into(imageView)
        }

        imageView.setOnClickListener {
            dismiss()
        }

        return view
    }
}
