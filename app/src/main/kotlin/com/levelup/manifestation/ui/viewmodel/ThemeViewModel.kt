package com.levelup.manifestation.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.manifestation.data.store.PrefsKeys
import com.levelup.manifestation.ui.theme.ThemeMode
import com.levelup.manifestation.ui.theme.ToneTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val tone: StateFlow<ToneTheme> = MutableStateFlow(ToneTheme.Default)

    val themeMode: StateFlow<ThemeMode> = dataStore.data
        .map { prefs ->
            when (prefs[PrefsKeys.THEME_MODE]) {
                "ethereal" -> ThemeMode.Ethereal
                else -> ThemeMode.Teal
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.Teal)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            dataStore.edit { it[PrefsKeys.THEME_MODE] = mode.name.lowercase() }
        }
    }
}
