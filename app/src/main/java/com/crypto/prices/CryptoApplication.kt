package com.crypto.prices

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class CryptoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: CryptoApplication? = null
            private set
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}