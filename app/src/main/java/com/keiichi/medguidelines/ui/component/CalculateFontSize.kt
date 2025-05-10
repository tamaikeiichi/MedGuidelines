package com.keiichi.medguidelines.ui.component

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

fun calculateFontSize(text: String): TextUnit {
    val baseSize = 28.sp
    val minSize = 12.sp
    val maxLength = 6

    return when {
        text.length <= maxLength / 2 -> baseSize
        text.length <= maxLength -> (baseSize.value - (text.length - maxLength / 2) * 6).sp
        else -> minSize
    }
}