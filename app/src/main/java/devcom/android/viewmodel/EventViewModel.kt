package devcom.android.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import devcom.android.utils.constants.FirebaseConstants

class EventViewModel: ViewModel() {

     val db = Firebase.firestore
     val events = MutableLiveData<ArrayList<devcom.android.data.Event>>()
     fun getEventData(contex:Context){
        db.collection(FirebaseConstants.COLLECTION_PATH_EVENTS).addSnapshotListener{ value, error ->
            if(error != null){
                Toast.makeText(contex, "beklenmedik bir hata oluştu!", Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    val documents = value.documents
                    Log.i("EventsDocuments",documents.toString())
                    //eventList.clear()
                    val eventList = ArrayList<devcom.android.data.Event>()

                    for(document in documents){


                        val eventDoc = document.id
                        val eventTitle = document.get(FirebaseConstants.FIELD_EVENT_TITLE) as? String
                        val eventDescription = document.get(FirebaseConstants.FIELD_EVENT_DESCRIPTION) as? String
                        val eventLocation = document.get(FirebaseConstants.FIELD_EVENT_LOCATION) as? String
                        val eventDate = document.getTimestamp(FirebaseConstants.FIELD_EVENT_DATE)?.toDate()
                        val createEventTime = document.getTimestamp(FirebaseConstants.FIELD_EVENT_CREATE_DATE)?.toDate()
                        Toast.makeText(contex,createEventTime.toString(), Toast.LENGTH_SHORT).show()

                        val event = devcom.android.data.Event(
                            eventDoc,
                            eventTitle,
                            eventDescription,
                            eventDate,
                            eventLocation,
                            createEventTime
                        )
                        eventList.add(event)
                    }

                    events.value = eventList
                }
            }
        }

    }


}