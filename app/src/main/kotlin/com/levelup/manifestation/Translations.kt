package com.levelup.manifestation

import android.content.Context
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.ToneTheme
import org.json.JSONObject

import java.util.concurrent.ConcurrentHashMap

object Translations {

    private lateinit var lang: JSONObject

    // Remote content overrides — populated by SheetsRepository (thread-safe)
    private val remoteAffirmations = ConcurrentHashMap<LifeArea, List<String>>()
    private val remotePrograms = ConcurrentHashMap<LifeArea, List<Pair<String, String>>>()

    fun load(context: Context) {
        if (::lang.isInitialized) return
        val json = context.assets.open("translations.json").bufferedReader().readText()
        lang = JSONObject(json).getJSONObject("ru")
    }

    fun updateAffirmations(area: LifeArea, strings: List<String>) {
        remoteAffirmations[area] = strings
    }

    fun updatePrograms(area: LifeArea, pairs: List<Pair<String, String>>) {
        remotePrograms[area] = pairs
    }

    fun ui(key: String): String =
        lang.optJSONObject("ui")?.optString(key)?.takeIf { it.isNotEmpty() } ?: key

    fun lifeAreaLabel(area: LifeArea): String {
        val key = area.name.replaceFirstChar { it.lowercase() }
        return lang.optJSONObject("lifeAreas")?.optString(key)?.takeIf { it.isNotEmpty() } ?: area.label
    }

    fun toneThemeName(tone: ToneTheme): String {
        return tone.displayName
    }

    fun affirmationStrings(area: LifeArea): List<String> {
        remoteAffirmations[area]?.let { return it }
        val key = area.name.replaceFirstChar { it.lowercase() }
        val arr = lang.optJSONObject("affirmations")?.optJSONArray(key) ?: return emptyList()
        return (0 until arr.length()).map { arr.getString(it) }
    }

    data class MeditationRaw(
        val id: String,
        val title: String,
        val fileName: String,
        val durationSeconds: Int
    )

    fun meditationData(area: LifeArea): List<MeditationRaw> {
        val key = area.name.replaceFirstChar { it.lowercase() }
        val arr = lang.optJSONObject("meditations")?.optJSONArray(key) ?: return emptyList()
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            MeditationRaw(
                id = obj.getString("id"),
                title = obj.getString("title"),
                fileName = obj.getString("fileName"),
                durationSeconds = obj.getInt("durationSeconds")
            )
        }
    }

    fun programPairs(area: LifeArea): List<Pair<String, String>> {
        remotePrograms[area]?.let { return it }
        val key = area.name.replaceFirstChar { it.lowercase() }
        val arr = lang.optJSONObject("programs")?.optJSONArray(key) ?: return emptyList()
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            obj.getString("limiting") to obj.getString("rewrite")
        }
    }
}
