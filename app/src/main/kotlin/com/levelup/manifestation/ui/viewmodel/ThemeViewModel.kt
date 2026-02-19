package com.levelup.manifestation.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.manifestation.data.store.PrefsKeys
import com.levelup.manifestation.ui.theme.ToneTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val tone = dataStore.data
        .map { prefs ->
            val saved = prefs[PrefsKeys.TONE_KEY]
            ToneTheme.entries.find { it.name == saved } ?: ToneTheme.SoftFeminine
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ToneTheme.SoftFeminine)

    fun setTone(newTone: ToneTheme) {
        viewModelScope.launch {
            dataStore.edit { it[PrefsKeys.TONE_KEY] = newTone.name }
        }
    }
}
