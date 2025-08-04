package com.bekisma.adlamfulfulde

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AdlamFulfuldeApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components here
    }
}