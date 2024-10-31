package com.example.nobrainer2

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.*

class CustomApplication : Application() {
    override fun attachBaseContext(base: Context) {
        val sharedPreferences = base.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "en") ?: "en"
        val locale = Locale(language)
        val config = Configuration(base.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        val context = base.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate() {
        super.onCreate()
    }
}