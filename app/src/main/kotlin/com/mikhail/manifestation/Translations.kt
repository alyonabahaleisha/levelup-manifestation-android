package com.mikhail.manifestation

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mikhail.manifestation.ui.theme.LifeArea
import com.mikhail.manifestation.ui.theme.ToneTheme
import org.json.JSONObject

import java.util.concurrent.ConcurrentHashMap

object Translations {

    private const val TAG = "Translations"

    private lateinit var lang: JSONObject

    // Remote content overrides — populated by Firestore listeners (thread-safe)
    private val remoteAffirmations = ConcurrentHashMap<LifeArea, List<String>>()
    private val remotePrograms = ConcurrentHashMap<LifeArea, List<Pair<String, String>>>()
    private val remoteMeditations = ConcurrentHashMap<LifeArea, List<MeditationRaw>>()
    private val remoteMusic = ConcurrentHashMap<String, MusicTrackRaw>()
    private val remoteUiStrings = ConcurrentHashMap<String, String>()

    private val listeners = mutableListOf<ListenerRegistration>()
    var onMeditationsLoaded: (() -> Unit)? = null

    fun load(context: Context) {
        if (::lang.isInitialized) return
        val json = context.assets.open("translations.json").bufferedReader().readText()
        lang = JSONObject(json).getJSONObject("ru")
    }

    /** Start Firestore snapshot listeners for real-time content updates */
    fun syncFromFirestore() {
        val db = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not configured, skipping sync", e)
            return
        }

        // Affirmations
        listeners += db.collection("affirmations").addSnapshotListener { snap, e ->
            if (e != null || snap == null) return@addSnapshotListener
            for (doc in snap.documents) {
                val area = LifeArea.entries.find { it.name.replaceFirstChar { c -> c.lowercase() } == doc.id }
                    ?: continue
                @Suppress("UNCHECKED_CAST")
                val texts = doc.get("texts") as? List<String> ?: continue
                remoteAffirmations[area] = texts
            }
        }

        // Programs
        listeners += db.collection("programs").addSnapshotListener { snap, e ->
            if (e != null || snap == null) return@addSnapshotListener
            for (doc in snap.documents) {
                val area = LifeArea.entries.find { it.name.replaceFirstChar { c -> c.lowercase() } == doc.id }
                    ?: continue
                @Suppress("UNCHECKED_CAST")
                val pairs = (doc.get("pairs") as? List<Map<String, String>>)?.map {
                    (it["limiting"] ?: "") to (it["rewrite"] ?: "")
                } ?: continue
                remotePrograms[area] = pairs
            }
        }

        // Meditations
        listeners += db.collection("meditations").addSnapshotListener { snap, e ->
            if (e != null || snap == null) return@addSnapshotListener
            val byArea = mutableMapOf<LifeArea, MutableList<MeditationRaw>>()
            for (doc in snap.documents) {
                val areaStr = doc.getString("area") ?: continue
                val area = LifeArea.entries.find { it.name.replaceFirstChar { c -> c.lowercase() } == areaStr }
                    ?: continue
                val raw = MeditationRaw(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    fileName = doc.getString("fileName") ?: "",
                    durationSeconds = doc.getLong("durationSeconds")?.toInt() ?: 0,
                    audioUrl = doc.getString("audioUrl") ?: "",
                    coverUrl = doc.getString("coverUrl") ?: "",
                    coverColor = doc.getString("coverColor") ?: "",
                    popular = doc.getBoolean("popular") ?: false
                )
                byArea.getOrPut(area) { mutableListOf() }.add(raw)
            }
            remoteMeditations.clear()
            remoteMeditations.putAll(byArea)
            onMeditationsLoaded?.invoke()
        }

        // Music
        listeners += db.collection("musicTracks").addSnapshotListener { snap, e ->
            if (e != null || snap == null) return@addSnapshotListener
            remoteMusic.clear()
            for (doc in snap.documents) {
                remoteMusic[doc.id] = MusicTrackRaw(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    artist = doc.getString("artist") ?: "",
                    fileName = doc.getString("fileName") ?: "",
                    durationSeconds = doc.getLong("durationSeconds")?.toInt() ?: 0,
                    audioUrl = doc.getString("audioUrl") ?: ""
                )
            }
        }

        // UI strings
        listeners += db.document("config/ui_strings").addSnapshotListener { snap, e ->
            if (e != null || snap == null || !snap.exists()) return@addSnapshotListener
            remoteUiStrings.clear()
            for ((key, value) in snap.data.orEmpty()) {
                if (value is String) remoteUiStrings[key] = value
            }
        }
    }

    fun updateAffirmations(area: LifeArea, strings: List<String>) {
        remoteAffirmations[area] = strings
    }

    fun updatePrograms(area: LifeArea, pairs: List<Pair<String, String>>) {
        remotePrograms[area] = pairs
    }

    fun ui(key: String): String =
        remoteUiStrings[key]
            ?: lang.optJSONObject("ui")?.optString(key)?.takeIf { it.isNotEmpty() }
            ?: key

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
        val description: String,
        val fileName: String,
        val durationSeconds: Int,
        val audioUrl: String = "",
        val coverUrl: String = "",
        val coverColor: String = "",
        val popular: Boolean = false
    )

    fun meditationData(area: LifeArea): List<MeditationRaw> {
        remoteMeditations[area]?.let { return it }
        val key = area.name.replaceFirstChar { it.lowercase() }
        val arr = lang.optJSONObject("meditations")?.optJSONArray(key) ?: return emptyList()
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            MeditationRaw(
                id = obj.getString("id"),
                title = obj.getString("title"),
                description = obj.optString("description", ""),
                fileName = obj.getString("fileName"),
                durationSeconds = obj.getInt("durationSeconds")
            )
        }
    }

    fun popularMeditations(): List<MeditationRaw> =
        remoteMeditations.values.flatten().filter { it.popular }

    data class MusicTrackRaw(
        val id: String,
        val title: String,
        val artist: String,
        val fileName: String,
        val durationSeconds: Int,
        val audioUrl: String = ""
    )

    fun musicData(): List<MusicTrackRaw> {
        if (remoteMusic.isNotEmpty()) {
            return remoteMusic.values.toList()
        }
        val arr = lang.optJSONArray("music") ?: return emptyList()
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            MusicTrackRaw(
                id = obj.getString("id"),
                title = obj.getString("title"),
                artist = obj.optString("artist", ""),
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
