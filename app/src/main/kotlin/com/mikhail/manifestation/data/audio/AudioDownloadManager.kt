package com.mikhail.manifestation.data.audio

// iOS Migration: -> AudioDownloadManager.swift — URLSession downloadTask, FileManager cache, @Published progress

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

enum class DownloadStatus { NotDownloaded, Downloading, Downloaded }

data class DownloadState(
    val status: DownloadStatus = DownloadStatus.NotDownloaded,
    val progress: Float = 0f // 0..1, -1 means indeterminate
)

@Singleton
class AudioDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cacheDir = context.cacheDir.resolve("audio_cache").also { it.mkdirs() }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _downloads = MutableStateFlow<Map<String, DownloadState>>(emptyMap())
    val downloads: StateFlow<Map<String, DownloadState>> = _downloads

    fun cacheFile(id: String): File = cacheDir.resolve("$id.mp3")

    fun isDownloaded(id: String): Boolean = cacheFile(id).exists()

    fun getLocalUrl(id: String): String? {
        val file = cacheFile(id)
        return if (file.exists()) file.toURI().toString() else null
    }

    fun downloadState(id: String): DownloadState {
        if (isDownloaded(id)) return DownloadState(DownloadStatus.Downloaded, 1f)
        return _downloads.value[id] ?: DownloadState()
    }

    fun download(id: String, url: String) {
        if (isDownloaded(id)) return
        if (_downloads.value[id]?.status == DownloadStatus.Downloading) return

        updateState(id, DownloadState(DownloadStatus.Downloading, 0f))

        scope.launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 15_000
                connection.readTimeout = 30_000
                connection.connect()

                val totalBytes = connection.contentLength.toLong()
                // totalBytes == -1 means the server did not send Content-Length → indeterminate
                val indeterminate = totalBytes <= 0

                val input = connection.inputStream
                val tempFile = File(cacheDir, "$id.tmp")
                val output = tempFile.outputStream()

                val buffer = ByteArray(8192)
                var downloaded = 0L
                var bytesRead: Int

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    downloaded += bytesRead
                    val progress = when {
                        indeterminate -> -1f // signal indeterminate to the UI
                        else -> (downloaded.toFloat() / totalBytes).coerceIn(0f, 1f)
                    }
                    withContext(Dispatchers.Main) {
                        updateState(id, DownloadState(DownloadStatus.Downloading, progress))
                    }
                }

                output.close()
                input.close()
                connection.disconnect()

                tempFile.renameTo(cacheFile(id))
                withContext(Dispatchers.Main) {
                    updateState(id, DownloadState(DownloadStatus.Downloaded, 1f))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateState(id, DownloadState(DownloadStatus.NotDownloaded, 0f))
                }
            }
        }
    }

    private fun updateState(id: String, state: DownloadState) {
        _downloads.value = _downloads.value.toMutableMap().apply { put(id, state) }
    }
}
