package com.languify

import android.app.Application
import com.languify.infra.storage.StorageProvider

class LanguifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StorageProvider.initialize(this)
    }
}
