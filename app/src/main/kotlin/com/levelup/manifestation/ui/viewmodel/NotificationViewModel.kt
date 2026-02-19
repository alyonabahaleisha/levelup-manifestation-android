package com.levelup.manifestation.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.manifestation.data.notifications.AffirmationReceiver
import com.levelup.manifestation.data.store.PrefsKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class NotifSettings(
    val isEnabled: Boolean = false,
    val startHour: Int = 9,
    val startMin: Int = 0,
    val endHour: Int = 18,
    val endMin: Int = 0,
    val intervalMinutes: Int = 60
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val settings = dataStore.data.map { prefs ->
        NotifSettings(
            isEnabled = prefs[PrefsKeys.NOTIF_ENABLED] ?: false,
            startHour = prefs[PrefsKeys.NOTIF_START_HOUR] ?: 9,
            startMin = prefs[PrefsKeys.NOTIF_START_MIN] ?: 0,
            endHour = prefs[PrefsKeys.NOTIF_END_HOUR] ?: 18,
            endMin = prefs[PrefsKeys.NOTIF_END_MIN] ?: 0,
            intervalMinutes = prefs[PrefsKeys.NOTIF_INTERVAL] ?: 60
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, NotifSettings())

    fun enable() {
        viewModelScope.launch {
            dataStore.edit { it[PrefsKeys.NOTIF_ENABLED] = true }
            scheduleNotifications()
        }
    }

    fun disable() {
        viewModelScope.launch {
            dataStore.edit { it[PrefsKeys.NOTIF_ENABLED] = false }
            cancelAll()
        }
    }

    fun updateStartTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            dataStore.edit {
                it[PrefsKeys.NOTIF_START_HOUR] = hour
                it[PrefsKeys.NOTIF_START_MIN] = minute
            }
            if (settings.value.isEnabled) scheduleNotifications()
        }
    }

    fun updateEndTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            dataStore.edit {
                it[PrefsKeys.NOTIF_END_HOUR] = hour
                it[PrefsKeys.NOTIF_END_MIN] = minute
            }
            if (settings.value.isEnabled) scheduleNotifications()
        }
    }

    fun updateInterval(minutes: Int) {
        viewModelScope.launch {
            dataStore.edit { it[PrefsKeys.NOTIF_INTERVAL] = minutes }
            if (settings.value.isEnabled) scheduleNotifications()
        }
    }

    fun scheduleNotifications() {
        val s = settings.value
        if (!s.isEnabled) return
        cancelAll()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        computedTimes(s).forEachIndexed { i, (hour, minute) ->
            val intent = Intent(context, AffirmationReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context, i, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pending)
        }
    }

    private fun cancelAll() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        repeat(60) { i ->
            val intent = Intent(context, AffirmationReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context, i, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pending)
        }
    }

    private fun computedTimes(s: NotifSettings): List<Pair<Int, Int>> {
        val startMin = s.startHour * 60 + s.startMin
        val endMin = s.endHour * 60 + s.endMin
        if (endMin <= startMin || s.intervalMinutes <= 0) return emptyList()
        val result = mutableListOf<Pair<Int, Int>>()
        var current = startMin
        while (current <= endMin && result.size < 60) {
            result.add(Pair(current / 60, current % 60))
            current += s.intervalMinutes
        }
        return result
    }
}
