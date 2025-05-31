package com.keiichi.medguidelines.ui.component

import android.icu.text.Transliterator
import java.util.Locale

fun normalizeTextForSearch(text: String): String {
    if (text.isBlank()) return ""

    var normalized = text.lowercase(Locale.getDefault())

    try {
        val fullToHalfTransliterator = Transliterator.getInstance("Fullwidth-Halfwidth")
        normalized = fullToHalfTransliterator.transliterate(normalized)

        // Katakana to Hiragana (makes matching easier if data/query uses mixed forms)
        val katakanaToHiraganaTransliterator = Transliterator.getInstance("Katakana-Hiragana")
        normalized = katakanaToHiraganaTransliterator.transliterate(normalized)
    } catch (e: Exception) {
        println("ICU Transliterator error: ${e.message}")
    }
    // 4. Optional: Remove whitespace or specific punctuation if needed
    //    normalized = normalized.replace("\\s+".toRegex(), "") // Example: remove all whitespace
    return normalized
}