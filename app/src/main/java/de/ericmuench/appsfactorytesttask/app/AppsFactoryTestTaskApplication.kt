package de.ericmuench.appsfactorytesttask.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class AppsFactoryTestTaskApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}