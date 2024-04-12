package devcom.android.ui.fragment.event.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import devcom.android.data.Event
import devcom.android.data.Question
import devcom.android.databinding.ItemEventsRowBinding
import devcom.android.databinding.ItemQuestionRowBinding
import java.lang.Long.max
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit


class EventAdapter(private var eventList: ArrayList<Event>) : RecyclerView.Adapter<EventAdapter.EventHolder>() {

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = TimeUnit.SECONDS.toMillis(60) // Her saniye

        private val updateRunnable = object : Runnable {
        override fun run() {
            updateCountdowns()
            handler.postDelayed(this, updateInterval)
        }
    }
    init {
        handler.postDelayed(updateRunnable, updateInterval)
    }
    class EventHolder(val binding: ItemEventsRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val binding =
            ItemEventsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventHolder(binding)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        holder.binding.tvEventHeader.text = eventList[position].eventHeader
        holder.binding.tvEventDescription.text = eventList[position].eventContent
        holder.binding.tvLocation.text = eventList[position].eventLocation

        holder.binding.progressBarEvent.setProgress(100)

        val countdown = eventList[position].calculateCountdown()
        if (countdown != null) {
            val (days, hours, minutes) = countdown
            holder.binding.progressBarText.text = "$days:$hours:$minutes"

            val currentDate = Date().time
            val eventDate = eventList[position].createdEventTimer?.time ?: 0
            val totalDuration = Math.abs(eventDate - currentDate)

            // Geriye kalan sürenin yüzdesini hesapla ve ProgressBar'ın değerini güncelle
            val remainingTime = max(0L, countdown.first * 24 * 60 * 60 * 1000 + countdown.second * 60 * 60 * 1000 + countdown.third * 60 * 1000)
            val progress = ((totalDuration - remainingTime) * 100 / totalDuration).toInt()
            Log.i("progressBar", progress.toString())
            holder.binding.progressBarEvent.setProgress(100 - progress)
        } else {
            holder.binding.progressBarText.text = "Geri sayım bilgisi bulunmuyor"
            holder.binding.progressBarEvent.progress = 0
        }
    }


    fun updateCountdowns() {
        for (event in eventList) {
            event.calculateCountdown() // Geri sayımı hesapla
        }
        notifyDataSetChanged() // RecyclerView'i yenile
    }


}