package com.mikhail.manifestation

import android.app.Application
import android.content.res.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.mikhail.manifestation.data.content.MeditationContent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LevelUpApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        forceRussianLocale()
        Translations.load(this)
        Translations.onMeditationsLoaded = { prefetchCovers() }
        Translations.syncFromFirestore()
        prefetchCovers()
    }

    private fun forceRussianLocale() {
        val locale = java.util.Locale("ru")
        java.util.Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.3)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .crossfade(true)
            .build()
    }

    private fun prefetchCovers() {
        val loader = imageLoader
        val meditations = MeditationContent.allMeditations()
        for (med in meditations) {
            val url = MeditationContent.coverUrl(med) ?: continue
            val request = ImageRequest.Builder(this)
                .data(url)
                .memoryCacheKey(url)
                .diskCacheKey(url)
                .size(coil.size.Size.ORIGINAL)
                .build()
            loader.enqueue(request)
        }
    }
}
