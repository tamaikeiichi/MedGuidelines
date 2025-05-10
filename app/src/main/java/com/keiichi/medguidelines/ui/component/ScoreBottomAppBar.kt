package com.keiichi.medguidelines.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun ScoreBottomAppBar(displayText: AnnotatedString) {
    ResultBottomAppBar {
        Text(
            text = displayText,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            lineHeight = 1.2.em
        )
    }
}