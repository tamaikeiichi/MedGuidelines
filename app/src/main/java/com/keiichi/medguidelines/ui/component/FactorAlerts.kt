package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * value: Double // if 0.0, the color is onError. If not 0.0, the color is onSecondary.
 */
@Composable
fun FactorAlerts(
    text: Int,
    value: Double // if 0.0, the color is onError.
) {
    val color = if (value != 0.0) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onError
    }
    Surface(
        color = color, // Set the background color to white
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(4.dp) // Optional: Add padding around the text
    ) {
        if (value != 0.0) {
            Text(
                text = parseStyledString(text),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(2.dp)
            )
        } else {
            Text(
                text = parseStyledString(text),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}