package com.levelup.manifestation.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import com.levelup.manifestation.ui.theme.ToneTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val tone: StateFlow<ToneTheme> = MutableStateFlow(ToneTheme.Default)

    fun setTone(newTone: ToneTheme) {
        // Single theme — no-op
    }
}
