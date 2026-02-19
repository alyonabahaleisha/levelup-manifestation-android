package com.levelup.manifestation.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "levelup_prefs")

object PrefsKeys {
    val TONE_KEY = stringPreferencesKey("selected_tone")
    val SAVED_PROGRAMS_KEY = stringPreferencesKey("saved_programs_v1")
    val NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")
    val NOTIF_START_HOUR = intPreferencesKey("notif_start_hour")
    val NOTIF_START_MIN = intPreferencesKey("notif_start_min")
    val NOTIF_END_HOUR = intPreferencesKey("notif_end_hour")
    val NOTIF_END_MIN = intPreferencesKey("notif_end_min")
    val NOTIF_INTERVAL = intPreferencesKey("notif_interval")
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}
