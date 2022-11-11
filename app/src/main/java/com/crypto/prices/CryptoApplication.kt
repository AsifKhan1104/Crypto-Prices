package com.crypto.prices

import android.app.Application

class CryptoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: CryptoApplication? = null
            private set
    }
}