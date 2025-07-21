package com.bekisma.adlamfulfulde

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class CustomTestRunner : AndroidJUnitRunner() {
    
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize test-specific configurations
    }
}