package com.levelup.manifestation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LevelUpApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Translations.load(this)
    }
}
