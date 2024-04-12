package devcom.android.ui.fragment.event

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import devcom.android.R
import devcom.android.data.Event
import devcom.android.data.Question
import devcom.android.ui.fragment.event.adapter.EventAdapter
import devcom.android.ui.fragment.form.FormFragmentDirections
import devcom.android.utils.constants.FirebaseConstants
import devcom.android.viewmodel.EventViewModel
import java.sql.Timestamp


class EventFragment : Fragment() {
    private val db = Firebase.firestore

    private lateinit var viewModel: EventViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var profileImageView: ImageView
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    var eventList: ArrayList<Event> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        eventRecyclerView = view.findViewById(R.id.rv_eventView)
        profileImageView = view.findViewById(R.id.iv_profile_event)

        viewModel.getEventData(requireContext())

        //getEventData()
        observeLiveData()


        auth = Firebase.auth
        getData()

        //Navigate to Profile
        profileImageView.setOnClickListener {
            val action = EventFragmentDirections.actionEventToProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

    }
    private fun observeLiveData(){
        viewModel.events.observe(viewLifecycleOwner, Observer {events ->
            setAdapter(events)
        })
    }
    private fun setAdapter(eventList:ArrayList<Event>) {
        Toast.makeText(requireContext(), eventList.size.toString(), Toast.LENGTH_SHORT).show()
        eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter(eventList)
        eventRecyclerView.adapter = eventAdapter
    }

    private fun getData() {

        db.collection(FirebaseConstants.COLLECTION_PATH_USERS).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), "beklenmedik bir hata oluştu.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (value != null) {

                    val documents = value.documents

                    for (document in documents) {

                        val uuid = document.get(FirebaseConstants.FIELD_UUID) as? String
                        val downloadUrl =
                            document.get(FirebaseConstants.FIELD_DOWNLOAD_URL) as? String


                        if (uuid == auth.currentUser!!.uid) {

                            if (downloadUrl != null) {
                                Picasso.get().load(downloadUrl).resize(200, 200).centerCrop()
                                    .into(profileImageView)
                            } else {
                                continue
                            }
                        }
                    }

                }
            }

        }
    }

}