package com.languify.core.localization

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleManager {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "app_language"

    // Chama isto na MainActivity para aplicar a língua guardada ao iniciar
    fun onAttach(context: Context): Context {
        val lang = getPersistedLanguage(context)
        return setLocale(context, lang)
    }

    // Chama isto quando o user clica num botão para mudar a língua
    fun setNewLocale(context: Context, language: String): Context {
        persistLanguage(context, language)
        return setLocale(context, language)
    }

    fun getPersistedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en" // "en" é o default
    }

    private fun persistLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    private fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            return context.createConfigurationContext(config)
        } else {
            // Para versões muito antigas do Android
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            return context
        }
    }
}