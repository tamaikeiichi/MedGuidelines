package com.keiichi.medguidelines.ui.component

import android.content.Context
import android.content.res.Configuration
import java.util.Locale


fun getStringOfSpecificLocale(context: Context, resId: Int, targetLocale: Locale): String {
    val currentLocale = context.resources.configuration.locale
    if (currentLocale == targetLocale) {
        // Optimization: If target is current, just use normal getString
        try {
            return context.getString(resId)
        } catch (e: Exception) {
            // Handle cases where the string might be missing for the current locale
            // (should ideally fall back to default, but explicit error handling is safer)
            println("Error getting string for current locale $targetLocale: ${e.message}")
            return "" // Or some default
        }
    }
    // Create a new configuration context for the target locale
    val config = Configuration(context.resources.configuration)
    config.setLocale(targetLocale)
    try {
        return context.createConfigurationContext(config).getString(resId)
    } catch (e: Exception) {
        // Handle cases where the string might be missing for the target locale
        println("Error getting string for target locale $targetLocale: ${e.message}")
        return "" // Or some default value if the string is not found
    }
}