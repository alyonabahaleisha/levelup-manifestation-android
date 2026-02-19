package com.levelup.manifestation.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.manifestation.data.model.Affirmation
import com.levelup.manifestation.data.model.HiddenProgram
import com.levelup.manifestation.data.store.PrefsKeys
import com.levelup.manifestation.ui.theme.LifeArea
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SavedProgramsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val saved = dataStore.data
        .map { prefs -> deserialize(prefs[PrefsKeys.SAVED_PROGRAMS_KEY]) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun save(program: HiddenProgram) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                val current = deserialize(prefs[PrefsKeys.SAVED_PROGRAMS_KEY]).toMutableList()
                if (current.none { it.text == program.rewrite }) {
                    current.add(0, Affirmation(text = program.rewrite, area = program.area, isPersonal = true))
                    prefs[PrefsKeys.SAVED_PROGRAMS_KEY] = serialize(current)
                }
            }
        }
    }

    fun isSaved(program: HiddenProgram): Boolean =
        saved.value.any { it.text == program.rewrite }

    private fun serialize(list: List<Affirmation>): String {
        val arr = JSONArray()
        list.forEach { a ->
            arr.put(JSONObject().apply {
                put("id", a.id)
                put("text", a.text)
                put("area", a.area.name)
            })
        }
        return arr.toString()
    }

    private fun deserialize(json: String?): List<Affirmation> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).mapNotNull { i ->
                val obj = arr.getJSONObject(i)
                val area = LifeArea.entries.find { it.name == obj.getString("area") } ?: return@mapNotNull null
                Affirmation(
                    id = obj.getString("id"),
                    text = obj.getString("text"),
                    area = area,
                    isPersonal = true
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
