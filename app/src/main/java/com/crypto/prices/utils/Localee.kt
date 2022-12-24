package com.crypto.prices.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.util.Log
import com.crypto.prices.model.LanguageData
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
        if (!locale.lowercase().contains("en")) {
            val langList = fetchLanguageList()
            var isLangSet = false
            for (lang in langList) {
                if (locale.lowercase().equals(lang.locale, true)) {
                    setLocale(context, lang.locale)
                    Utility.setLanguage(context as Activity, lang.name)
                    Utility.setLanguageLocale(context as Activity, lang.locale)
                    Log.d("featLanguage", lang.name + " saved in shared prefs")
                    isLangSet = true
                }
            }
            // if device lang is not in supported lang list
            // set "en" locale by default
            if (!isLangSet) {
                setLocale(context, "en")
                Utility.setLanguage(context as Activity, "English")
                Utility.setLanguageLocale(context as Activity, "en")
                Log.d("featLanguage", "English saved in shared prefs")
            }
        }
    }

    // language list
    fun fetchLanguageList(): List<LanguageData> {
        val languageList = ArrayList<LanguageData>()
        languageList.add(LanguageData("عربى", "ar"))
        languageList.add(LanguageData("Deutsche", "de"))
        languageList.add(LanguageData("English", "en"))
        languageList.add(LanguageData("Español", "es"))
        languageList.add(LanguageData("Français", "fr"))
        languageList.add(LanguageData("ગુજરાતી", "gu"))
        languageList.add(LanguageData("हिंदी", "hi"))
        languageList.add(LanguageData("Magyar", "hu"))
        languageList.add(LanguageData("bahasa Indonesia", "id"))
        languageList.add(LanguageData("Italiano", "it"))
        languageList.add(LanguageData("日本", "ja"))
        languageList.add(LanguageData("ಕನ್ನಡ", "kn"))
        languageList.add(LanguageData("한국인", "ko"))
        languageList.add(LanguageData("Nederlands", "nl"))
        languageList.add(LanguageData("Polski", "pl"))
        languageList.add(LanguageData("Română", "ro"))
        languageList.add(LanguageData("Svenska", "sv"))
        languageList.add(LanguageData("தமிழ்", "ta"))
        languageList.add(LanguageData("తెలుగు", "te"))
        languageList.add(LanguageData("Türkçe", "tr"))
        languageList.add(LanguageData("中国人", "zh"))
        return languageList
    }
}