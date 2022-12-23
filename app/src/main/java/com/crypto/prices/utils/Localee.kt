package com.crypto.prices.utils

import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.*

object Localee {
    fun setLocale(context: Context, loc: String) {
        val locale = Locale(loc)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun getLocale(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault().get(0).language
        } else {
            Locale.getDefault().language;
        }
    }

    // sets locale as per device language
    fun handleLocale(context: Context) {
        val locale = getLocale().lowercase()
        if (!locale.contains("en")) {
            setLocale(context, locale)
        }
    }
}