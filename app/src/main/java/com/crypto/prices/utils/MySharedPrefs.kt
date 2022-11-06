package com.crypto.prices.utils

import android.content.Context
import android.content.SharedPreferences

internal class MySharedPrefs private constructor(context: Context) {
    companion object : SingletonHolder<MySharedPrefs, Context>(::MySharedPrefs)

    private var prefs: SharedPreferences =
        context.getSharedPreferences("my-prefs", Context.MODE_PRIVATE)

    internal fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().apply { this.putBoolean(key, value).apply() }
    }

    internal fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    internal fun saveLong(key: String, value: Long) {
        prefs.edit().apply { this.putLong(key, value).apply() }
    }

    internal fun getLong(key: String): Long? {
        return prefs.getLong(key, 0L)
    }
}