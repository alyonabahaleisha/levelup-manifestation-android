package com.levelup.manifestation.data.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.levelup.manifestation.data.store.PrefsKeys
import com.levelup.manifestation.data.store.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs = runBlocking { context.dataStore.data.first() }
        val isEnabled = prefs[PrefsKeys.NOTIF_ENABLED] ?: false
        if (!isEnabled) return

        val startHour = prefs[PrefsKeys.NOTIF_START_HOUR] ?: 9
        val startMin = prefs[PrefsKeys.NOTIF_START_MIN] ?: 0
        val endHour = prefs[PrefsKeys.NOTIF_END_HOUR] ?: 18
        val endMin = prefs[PrefsKeys.NOTIF_END_MIN] ?: 0
        val interval = prefs[PrefsKeys.NOTIF_INTERVAL] ?: 60

        val startTotal = startHour * 60 + startMin
        val endTotal = endHour * 60 + endMin
        if (endTotal <= startTotal || interval <= 0) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var current = startTotal
        var i = 0
        while (current <= endTotal && i < 60) {
            val h = current / 60
            val m = current % 60
            val intentAlarm = Intent(context, AffirmationReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context, i, intentAlarm,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pending)
            current += interval
            i++
        }
    }
}
