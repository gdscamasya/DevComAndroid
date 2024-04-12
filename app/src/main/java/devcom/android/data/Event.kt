package devcom.android.data

import android.util.Log
import java.util.Date
import java.util.concurrent.TimeUnit

data class Event(
    val docNum: String?,
    val eventHeader: String?,
    val eventContent: String?,
    val eventTimer: Date?,
    val eventLocation: String?,
    val createdEventTimer: Date?
) {
    fun calculateCountdown():Triple<Long, Long, Long>? {
        Log.i("Eevenets",createdEventTimer.toString())
        if (eventTimer == null) {
            return null
        }

        val currentDate = Date().time // Şu anki zamanın milisaniye cinsinden değeri
        val eventTime = eventTimer.time // Etkinlik zamanının milisaniye cinsinden değeri
        Log.i("Eevents", eventTime.toString())
        val countdown = eventTime - currentDate
        // Geri sayımı hesapla (milisaniye cinsinden)
        // Geri sayımı gün, saat ve dakika olarak ayrıştır
        val days = TimeUnit.MILLISECONDS.toDays(countdown)
        val hours = TimeUnit.MILLISECONDS.toHours(countdown) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(countdown) % 60

        return Triple(days, hours, minutes)
    }
}