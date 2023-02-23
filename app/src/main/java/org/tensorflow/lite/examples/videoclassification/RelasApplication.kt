package org.tensorflow.lite.examples.videoclassification

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RelasApplication : Application() {
    private val notificationManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) this.getSystemService(
            NotificationManager::class.java
        )
        else null
    }


    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }



    override fun onCreate() {
        super.onCreate()


    }

}