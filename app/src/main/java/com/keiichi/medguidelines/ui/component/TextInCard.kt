package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun TextInCard(
    text: Int,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    ) {
    Card(
        colors = CardDefaults.cardColors(color),
        modifier = Modifier
            .cardModifier()
    ) {
        Text(
            text = parseStyledString(text),
            modifier = Modifier
                .padding(10.dp)
        )
    }
}