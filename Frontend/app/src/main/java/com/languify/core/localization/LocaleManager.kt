package com.languify.core.localization

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

/**
 * Handles changing app language at runtime.
 */
object LocaleManager {

    fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}
